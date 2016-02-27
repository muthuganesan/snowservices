# Gravity Ringer #

This is a program which silences your phone based on the pitch the phone makes. For example: I want my phone to be silenced when I put it upside down in my pants. I'll automagically go from audibly ringing to vibrate only when it's upside down for a couple of secs. The angles and delay can be set.

![http://snowservices.googlecode.com/svn/wiki/screens/GravityRingerScreen0.png](http://snowservices.googlecode.com/svn/wiki/screens/GravityRingerScreen0.png)


# Notes #

In the svn is a version which is able to be used with the [OpenIntents](http://www.openintents.org) [sensor simulator](http://www.openintents.org/en/node/6), just get the source from svn and change the `eu.MrSnowflake.android.gravityringe.GravityRinger.USE_ANDROID_SENSORS` to false and compile. This will give you a version which works together with the sensor simulator.