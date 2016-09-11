# FlexLayout
flex layout in Android!

##Usage
- Step 1
  Add these to your project's build.gradle
```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
- Step 2
  Add dependency
```
dependencies {
		compile 'com.github.houxg:FlexLayout:1.2'
	}
```

##Attrs
- justify-content
- align-content
- align-item
- rowGap
- colGap

##Example
###Flex
![](https://raw.githubusercontent.com/houxg/ScreenShot/master/FlexLayout/flex.jpg)
---
###Justify center
![](https://raw.githubusercontent.com/houxg/ScreenShot/master/FlexLayout/justify_center.jpg)
---
###Align item center
![](https://raw.githubusercontent.com/houxg/ScreenShot/master/FlexLayout/align_item_center.jpg)
---
###Justify space between
![](https://raw.githubusercontent.com/houxg/ScreenShot/master/FlexLayout/justify_space_between.jpg)
---
###Justify space around
![](https://raw.githubusercontent.com/houxg/ScreenShot/master/FlexLayout/justify_space_around.jpg)
