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

if (-not $targets) { Write-Output "  nothing to kill" }
