# FlexLayout
flex layout in Android!

##Attrs
- justify-content
- align-content
- align-item
- rowDividerHeight
- itemDividerWidth

##Example
###Flex
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/flex.jpg)

![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/flex2.jpg)

###Justify center
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/justify_center.jpg)

###Align item center
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/align_item_center.jpg)

###Justify space between
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/justify_space_between.jpg)

###Justify space around
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/justify_space_around.jpg)
##Useage
```
  <org.houxg.flexlayout.FlexLayout
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:align_item="center"
        app:itemDividerWidth="5dp"
        app:justify_content="start"
        app:rowDividerHeight="5dp"
        app:align_content="space_between">

　　　　...views that you want to be flex...

   </org.houxg.flexlayout.FlexLayout>
```