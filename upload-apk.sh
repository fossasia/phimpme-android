#!/usr/bin/env bash

mkdir $HOME/daily/
cd app/build/outputs/apk/
ls
cp -R app-debug-unaligned.apk $HOME/daily/
# go to home and setup git
cd $HOME
git config --global user.email "pawanpal004@gmail.com"
git config --global user.name "pa1pal"
  
git clone --quiet --branch=uploadapk https://pa1pal:$GITHUB_API_KEY@github.com/pa1pal/phimpme-android uploadapk > /dev/null
cd uploadapk
cp -Rf $HOME/daily/*  sample-apk/
git add -f .
  # git remote rm origin
  # git remote add origin https://the-dagger:$GITHUB_API_KEY@github.com/the-dagger/open-event-android
  git add -f .
  git commit -m "Update Sample Apk [skip ci]"
  git push origin uploadapk > /dev/null
