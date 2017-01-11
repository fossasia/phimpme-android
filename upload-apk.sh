#!/usr/bin/env bash
#create a new directory that will contain out generated apk
mkdir $HOME/buildApk/ 
#copy generated apk from build folder to the folder just created
cp -R app/build/outputs/apk/app-debug.apk $HOME/buildApk/
#setup git
cd $HOME
git config --global user.email "noreply@travis.com"
git config --global user.name "Travis CI" 
#clone the repository in the buildApk folder
git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/phimpme-android apk > /dev/null
cd apk  
#copy the data in the sample-apk folder in the branch apk
cp -Rf $HOME/buildApk/* sample-apk/ 
#add files
git add -f .
#commit and skip the tests
git commit -m "Travis build pushed [skip ci]"
#push to the branch apk
git push -fq origin apk > /dev/null
echo -e "Apk updated"
