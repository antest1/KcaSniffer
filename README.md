# KcaSniffer

<img src="https://upload.cc/i/lf5Vup.png" width="256"/>
VpnService-based Common Sniffer Tool for Kancolle Android

About Project
-------
This project is an open-source sniffer to capture request/response packets from Kancolle in Android.  

Since Android system only allows one VpnService, there was a difficulty to use multiple applications which need to get real-time kancolle data. 
So, this project aims to support real-time data to multiple 3rd-party kancolle tools,
and enables other developers to make various kancolle-related applications (like viewers, material logger, etc)
without knowledge of android system or network implementation details.

※ This application is under development. The data specification may be different after some updates.  
※ The icon is temporal generated in [Here](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html).
I'll update it as available.

Requirement
-------
- Android 4.1(JellyBean) or later
- Supporting Android VPN service
- Warning: Kcanotify may not work for specific devices(vendor-dependent issue) and old devices(Android 4.x).

Download & How to use
-------
You can download it at [Github Releases](https://github.com/antest1/KcaSniffer/releases)  
Just click the button on the screen, Press OK in the dialog at first. That's all.

Specification
-------
The specification is available [here](specification.md).
You can use this application with your own project!

Attribution
-------
- This project uses the modified source code of [NetGuard](https://github.com/M66B/NetGuard/) for capturing the traffic of Kancolle application.   
The license for NetGuard is [GNU General Public License version 3](http://www.gnu.org/licenses/gpl.txt).
- Many parts of the source code is from [Kcanotify](https://github.com/antest1/kcanotify).

Contact
-------
For question, suggestion and error report, make an [Issue](https://github.com/antest1/KcaSniffer/issues) or mail to me.  
E-mail: kcanotify@gmail.com

License
-------
[GNU General Public License version 3](http://www.gnu.org/licenses/gpl.txt)  
Copyright (c) 2018 antest1(IE10)  
All rights reserved
