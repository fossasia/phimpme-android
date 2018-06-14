#!/usr/bin/env bash
#create a new directory that will contain out generated apk
mkdir $HOME/buildApk/ 
#copy generated apk from build folder and README.md to the folder just created
cp -R app/build/outputs/apk/app-debug.apk $HOME/buildApk/
cp -R README.md $HOME/buildApk/

#setup git
cd $HOME
git config --global user.email "noreply@travis.com"
git config --global user.name "Travis CI" 
#clone the repository in the buildApk folder
git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/phimpme-android apk > /dev/null
cp -Rf $HOME/buildApk/*
cd apk

git checkout --orphan workaround
git add -A

#commit and skip the tests

git commit -am "Travis build pushed [skip ci]"

git branch -D apk
git branch -m apk

#push to the branch apk
git push origin apk --force --quiet> /dev/null
