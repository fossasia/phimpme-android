#!/usr/bin/env bash
# This script is for forks to upload their apks to their apk branch for danger to fetch them.
# The script which uploads builded apk from travis to the apk branch in the main repository is
# upload-apk.sh. This script was build so that in every PR the apk can be attached as comment
# via danger. The use of using two seperate scripts is to ensure that the token doesnot needed
# to be changed after every pull request merge. Travis will call this script if not in the main
# repository i.e, phimpme-android.

# Create a new directory that will contain out generated apk
mkdir $HOME/buildApk/

# Copy generated apk from build folder to the folder just created
cp -R app/build/outputs/apk/app-debug.apk $HOME/buildApk/

# Setting up git
cd $HOME
git config --global user.email "noreply@travis.com"
git config --global user.name "Travis CI"

# Clone the fork into the buildApk folder
# *** Important: For everychange pls update the below line in the following format:
# git clone --quiet --branch=apk htpps://username:authentiToken@github.com/username/phimpme-android apk > /dev/null
# replacing username with your github username and authentiToken with a personal token for your account.***
# Your token is advised to have only access to public repositories. Its only needed.
git clone --quiet --branch=apk https://CloudyPadmal:b1ff8e722d4466346d80b011fe0f2b532d2a3db3@github.com/CloudyPadmal/phimpme-android  apk > /dev/null
cp -Rf $HOME/buildApk/*
cd apk

git checkout --orphan workaround
git add -A

#add files
#git add -f .
#commit and skip the tests

git commit -am "Travis build pushed [skip ci]"

git branch -D apk
git branch -m apk

#push to the branch apk
git push origin apk --force --quiet> /dev/null
