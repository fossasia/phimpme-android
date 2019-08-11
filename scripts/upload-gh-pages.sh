#!/bin/sh

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@fossasia.org"

sudo pip install Sphinx

cd docs/sources

make html

git clone --quiet --branch=gh-pages https://fossasia:$GITHUB_API_KEY@github.com/fossasia/phimpme-android gh-pages > /dev/null
cd gh-pages

rm -rf *
cp -r ../_build/html/* .

git checkout --orphan temporary

git add --all .
git commit -am "[Auto] Update GH-Pages ($(date +%Y-%m-%d.%H:%M:%S))"

git branch -D gh-pages
git branch -m gh-pages

git push origin gh-pages --force --quiet > /dev/null
