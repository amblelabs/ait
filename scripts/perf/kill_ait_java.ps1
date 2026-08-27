# Stops the dev client and server for this project.
#
# A script file rather than -Command: the filter needs single quotes around 'java.exe', and bash
# single-quoting mangles those into nothing, which produced an invalid WMI filter that failed
# silently and left the old processes running.
$targets = Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" |
    Where-Object { $_.CommandLine -like "*loom-cache*" -and $_.CommandLine -like "*ait*" }

foreach ($p in $targets) {
    $kind = if ($p.CommandLine -like "*env=server*") { "server" }
            elseif ($p.CommandLine -like "*env=client*") { "client" }
            else { "other" }
    Write-Output "  killing $kind pid $($p.ProcessId)"
    Stop-Process -Id $p.ProcessId -Force -ErrorAction SilentlyContinue
}

# Also stop any gradle launcher still queued to start a run. One of these, left over from an
# interrupted job and blocked on the build lock, started a second client mid-measurement: with two
# players, "profile-client @a" profiles both and each writes its own dump.
$launchers = Get-CimInstance Win32_Process -Filter "Name = 'java.exe'" |
    Where-Object { $_.CommandLine -like "*GradleWrapperMain*" -and
                   ($_.CommandLine -like "*runClient*" -or $_.CommandLine -like "*runServer*") }

foreach ($p in $launchers) {
    Write-Output "  killing queued gradle launcher pid $($p.ProcessId)"
    Stop-Process -Id $p.ProcessId -Force -ErrorAction SilentlyContinue
}

if (-not $targets -and -not $launchers) { Write-Output "  nothing to kill" }
