TintedTranslucentStatusbar
==========================
Current build status for newest commit:
[![Build Status](https://travis-ci.org/woalk/TintedTranslucentStatusbar.svg?branch=master)](https://travis-ci.org/woalk/TintedTranslucentStatusbar)

##Xposed Framework Module for Android, to tint the statusbar on 4.4+ with gradient
This module is a powerful style changer for Android 4.4.x and 5.0.x (and most likely 5.x),
which allows to add a color to the StatusBar and NavigationBar in every app.

The color is made by making the SystemUI bar(s) [translucent](http://developer.android.com/about/versions/android-4.4.html#UI).
The SystemUI bar gets semi-transparent and a colored `View` is shown behind it, to make it appear tinted.

Because of the use of system APIs for this feature, most situations are already covered by a nice blending animation and good performance.

Also, this app can make content scrollable behind the NavigationBar while the bar is transparent without tint.

This all has one negative aspect: Every app needs an own setting configuring the app's layout.
Adding translucency results often in destroyed layouts due to content moving under the SystemUI bars.

There also is no auto-detection for the color at the moment
(and maybe there won't be in te future).

To help with circumventing those two issues, there is a thing called **TTSB Community**,
where users can submit their settings for apps and other users can download them for free.

There are many users using the Community and many apps included already.

###Support

**XDA Thread:**
http://forum.xda-developers.com/xposed/modules/mod-tinted-translucent-statusbar-beta-1-t2778937

###Copyright info
Uses code from SystemBarTint by Jeff Gilfelt, https://github.com/jgilfelt/SystemBarTint
(licensed under Apache License), with some small code changes in `SystemBarTint.java`.

The module itself is licensed under GNU GPL v2.
There is a copy of this license under `LICENSE` in this repository.

```
(C) 2014-2015 Woalk Software     woalk.com
              GitHub user woalk  github.com/woalk

Licensed under GNU General Public License v2.
Provided AS IS, without any warranty or liability for any damage.
```
