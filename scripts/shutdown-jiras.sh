#!/bin/sh
# -----------------------------------------------------------------------------
# Stop script for all test-jiras. All test JIRA needs to be installed under a JIRA* named directory for this to work.
# -----------------------------------------------------------------------------
echo "Test-JIRAs installation folder $1"
ls -d $1/JIRA*/bin/shutdown.sh |xargs -I {} bash {}
rm -f $1/home/.jira-home.lock
sleep 20