# phimpme-android

Phimp.me is a Photo App for Android that aims to replace proprietary photo applications. It offers features such as taking photos, adding filters, editing images and uploading them to social networks.

[![Build Status](https://travis-ci.org/fossasia/phimpme-android.svg?branch=master)](https://travis-ci.org/fossasia/phimpme-android)
[![codecov](https://codecov.io/gh/fossasia/phimpme-android/branch/master/graph/badge.svg)](https://codecov.io/gh/fossasia/phimpme-android)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ad1ba4cbecf04d3baa96a04c9a14d8cc)](https://www.codacy.com/app/mb/phimpme-android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fossasia/phimpme-android&amp;utm_campaign=Badge_Grade)

## Communication
Please join our chat channel on Slack: http://fossasia.slack.com/messages/phimpme/. You need to invite yourself first here: http://fossasia-slack.herokuapp.com/

## How to Contribute
This is an Open Source project and we would be happy to see contributors who report bugs and file feature requests submitting pull requests as well. Please report issues here https://github.com/fossasia/phimpme-android/issues

### Branch Policy
We have the following branches
 * **development**
	 All development goes on in this branch. If you're making a contribution,
	 you are supposed to make a pull request to _development_.
	 Make sure it pass a build check on Travis
 * **master**
   This contains the stable code. After significant features/bugfixes are accumulated on development, we move it to master.
 
 * **apk**
   This branch contains automatically generated apk file for testing.
   
## Screenshots

  <table>
    <tr>
     <td><img src="https://raw.githubusercontent.com/fossasia/phimpme-android/master/docs/screenshots/s1.png"></td></td>
     <td><img src="https://raw.githubusercontent.com/fossasia/phimpme-android/master/docs/screenshots/s2.png"></td>
     <td><img src="https://raw.githubusercontent.com/fossasia/phimpme-android/master/docs/screenshots/s3.png"></td>
     <td><img src="https://raw.githubusercontent.com/fossasia/phimpme-android/master/docs/screenshots/s4.png"></td>
    </tr>
  </table>

## Features
**Feature**|**Description**|**Status**
-----|-----|-----
Home Screen|Show local captured images|Need Enhancement
 |Grid view of images.|Need Enhancement
 |Edit, Upload, share option on image click|Working
Map Activity|Show photos on the map|Not working
Camera|Capture Image, Toggle Camera|Working
 |Apply filters, Flash on/off|Working
Upload|Select account on which you want to upload|Not working
 |Select images which you want to upload|Crashing
 |Share images with bluetooth|Working
 |Upload button which upload the image|Crashing
Settings|Add Accounts|Not working
 |Photos settings|Working
 |Local photos : choose folder|Not working
## Development Setup

Before you begin, you should already have the Android Studio SDK downloaded and set up correctly. You can find a guide on how to do this here: [Setting up Android Studio](http://developer.android.com/sdk/installing/index.html?pkg=studio)

**Setting up the Android Project**

1. Download the *phimpme-android* project source. You can do this either by forking and cloning the repository (recommended if you plan on pushing changes) or by downloading it as a ZIP file and extracting it.

2. Open Android Studio, you will see a **Welcome to Android** window. Under Quick Start, select *Import Project (Eclipse ADT, Gradle, etc.)*

3. Navigate to the directory where you saved the phimpme-android project, select the root folder of the project (the folder named "phimpme-android"), and hit OK. Android Studio should now begin building the project with Gradle.

4. Once this process is complete and Android Studio opens, check the Console for any build errors.

    - *Note:* If you recieve a Gradle sync error titled, "failed to find ...", you should click on the link below the error message (if avaliable) that says *Install missing platform(s) and sync project* and allow Android studio to fetch you what is missing.

5. Once all build errors have been resolved, you should be all set to build the app and test it.

6. To Build the app, go to *Build>Make Project* (or alternatively press the Make Project icon in the toolbar).

7. If the app was built succesfully, you can test it by running it on either a real device or an emulated one by going to *Run>Run 'app'* or presing the Run icon in the toolbar.

## License

This project is currently licensed under the GNU General Public License v3. A copy of [LICENSE](LICENSE.md) is to be present along with the source code. To obtain the software under a different license, please contact FOSSASIA.

## Maintainers
The project is maintained by
- Hon Nguyen ([@vanhonit](https://github.com/vanhonit))
- Mario Behling ([@mariobehling](http://github.com/mariobehling))
- Pawan Pal ([@pa1pal](http://github.com/pa1pal))
