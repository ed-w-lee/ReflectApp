# ReflectApp

## What is this app?
This app is intended to help users quantify their lives and ensure they keep
track of their metrics.  It does this by getting the user to fill out a daily
survey at a time of their choosing, enforcing this by preventing the user from
accessing non-system apps during this period.  Non-system apps are blocked in
case of emergencies and there is a mechanism to temporarily stop app-blocking in
case of some unforessen circumstance.

## Why did you make this?
I realized that I have a lot of long-term goals I want to achieve, but during
the day-to-day it's easy to forget what I want to accomplish. Thus, this is a
way of holding myself accountable in keeping track of things like:
* my progress on studying for the GRE
* keeping a good fitness schedule
* making sure I eat healthy
* continue learning

Also, it seemed like a good side project to keep me productive during spring
break, and let me play around with Android primarily and hopefully other
technologies (web, NLP, etc.) in the future.

## What features are you adding next?
Highest being most important:
* **survey tracking** - create the activity that will allow users to fill in a
  survey and store those responses in some database
* **customizable surveys** - Users should be able to create their own surveys
  that they want to fill out with metrics they want to track.
* **clean up code** - hopefully make it easier to navigate
* **add tests** - figure out how to actually do tests and whatnot
* **data visualization** - some way of graphing responses over time
* **blocking levels** - have different levels of blocking (all apps blocked, no
  apps blocked, obnoxious notification, etc.)
* **app whitelist** - don't just allow only system apps (which includes apps
  like Youtube, Photos, etc.) allow for a user-defined whitelist
* **web sync** - sync up responses to some web server
