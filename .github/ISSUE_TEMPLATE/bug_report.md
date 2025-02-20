name: Bug report
description: Create a report to help us improve
title: "[BUG] <title>"
labels: bug
assignees: 'Loqor'
body:
- type: checkboxes
  attributes:
  label: Is there an existing issue for this?
  description: Please search to see if an issue already exists for the bug you encountered.
  options:
  - label: I have searched the existing issues
    required: true
- type: textarea
  attributes:
  label: Describe the bug
  description: A clear and concise description of what the bug is.
  validations:
  required: true

- type: textarea
  attributes:
  label: To Reproduce
  description: Steps to reproduce the behavior:
  placeholder: |
    1. Go to '...'
    2. Click on '....'
    3. Scroll down to '....'
    4. See error

- type: input
  attributes:
  label: Screenshots / Logs
  description: If applicable, add screenshots or logs to help explain your problem.

- type: dropdown
  id: ait-version
  attributes:
  label: AIT Version
  description: Provide the version of AIT this is on:
  options:
    - 1.2.0
    - 1.1.0
    - 1.0.0
    - Beta-Release (List which version in Additional context)

- type: checkboxes
  id: git-issue
  attributes:
  label: Github Issue
  description: Is there a problem on the Github Repo?
  options:
    - label: Yes
    - label: No

- type: textarea
  attributes:
  label: Additional context
  description:  Add any other context about the problem here -  eg other mods you were playing with.
    
  
