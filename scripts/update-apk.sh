#!/bin/bash
set -e

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-development}
export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/phimpme-android" ] || ! [ "$TRAVIS_BRANCH" == "$DEPLOY_BRANCH" -o "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
	echo "We upload apk only for changes in development or master, and not PRs. So, let's skip this shall we ? :)"
	exit 0
fi


git clone --quiet --branch=apk https://fossasia:$GITHUB_API_KEY@github.com/fossasia/phimpme-android apk > /dev/null
cd apk

if [[ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]]; then
	/bin/rm -f *
else
	/bin/rm -f phimpme-dev-*.apk
fi

\cp -r ../app/build/outputs/apk/*/**.apk .

# Signing Apps

if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    echo "Push to master branch detected, signing the app..."
    cp app-release-unsigned.apk app-release-unaligned.apk
    jarsigner -tsa http://timestamp.comodoca.com/rfc3161 -sigalg SHA1withRSA -digestalg SHA1 -keystore ../scripts/key.jks -storepass $STORE_PASS -keypass $KEY_PASS app-release-unaligned.apk $ALIAS
	${ANDROID_HOME}/build-tools/${ANDROID_BUILD_TOOLS_VERSION}/zipalign -p 4 app-release-unaligned.apk app-release.apk
fi

if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
	for file in app*; do
		if [[ $file = "phimpme"* ]]; then
			continue
		fi
		mv $file phimpme-master-${file%%}
	done
fi

if [ "$TRAVIS_BRANCH" == "$DEPLOY_BRANCH" ]; then
	for file in app*; do
		if [[ $file = "phimpme"* ]]; then
			continue
		fi
		mv $file phimpme-dev-${file%%}
	done
fi

# Create a new branch that will contain only latest apk
git checkout --orphan temporary

# Add generated APK
git add --all .
git commit -am "[Auto] Update Test Apk ($(date +%Y-%m-%d.%H:%M:%S))"

# Delete current apk branch
git branch -D apk
# Rename current branch to apk
git branch -m apk

# Force push to origin since histories are unrelated
git push origin apk --force --quiet > /dev/null

# Publish App to Play Store
if [ "$TRAVIS_BRANCH" != "$PUBLISH_BRANCH" ]; then
    echo "We publish apk only for changes in master branch. So, let's skip this shall we ? :)"
    exit 0
fi

gem install fastlane
fastlane supply --apk phimpme-master-app-release.apk --track alpha --json_key ../scripts/fastlane.json --package_name $PACKAGE_NAME
