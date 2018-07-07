#!/usr/bin/env bash

if [ "$COMPONENT" == "ASSEMBLE_RELEASE" ]; then
  # Create a new directory that will contain out generated apk
  mkdir $HOME/buildApk/

  # Copy generated apk from build folder and README.md to the folder just created
  cp -R app/build/outputs/apk/app-debug.apk $HOME/buildApk/
  cp -R README.md $HOME/buildApk/

  # Setup git
  cd $HOME
  git config --global user.email "noreply@travis.com"
  git config --global user.name "Travis CI" 

  # Clone the repository in the buildApk folder
  git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/phimpme-android apk > /dev/null
  cp -Rf $HOME/buildApk/*
  cd apk

  git checkout --orphan workaround
  git add -A

  # Commit and skip the tests for that commit
  git commit -am "Travis build pushed [skip ci]"

  git branch -D apk
  git branch -m apk

  # Push to the apk branch
  git push origin apk --force --quiet> /dev/null
fi

exit 0

