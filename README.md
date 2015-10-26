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
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/flex.png)

![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/flex2.png)

###Justify center
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/justify_center.png)

###Align item center
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/align_item_center.png)

###Justify space between
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/justify_space_between.png)

###Justify space around
![](https://raw.githubusercontent.com/houxg/FlexLayout/master/screenshot/justify_space_around.png)
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