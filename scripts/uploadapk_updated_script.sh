#!/usr/bin/env bash

if [ "$COMPONENT" == "ASSEMBLE_RELEASE" ]; then
  

  git config --global user.name "Travis CI"
  git config --global user.email "noreply@travis.com"


  git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/phimpme-android apk > /dev/null
  cd apk
  \cp -r ../app/build/outputs/apk/app-debug.apk app-debug.apk
  \cp -r ../app/build/outputs/apk/app-release-unsigned.apk app-release-unsigned.apk

  # Create a new branch that will contains only latest apk
  git checkout --orphan temporary

  # Add generated APK
  git add --all .
  git commit -am "[Auto] Update Test Apk "

  # Delete current apk branch
  git branch -D apk
  # Rename current branch to apk
  git branch -m apk

  # Force push to origin since histories are unrelated
  git push origin apk --force --quiet > /dev/null
  
fi

exit 0

