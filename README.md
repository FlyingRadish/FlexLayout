# FlexLayout
flex layout in Android!

###attrs:
---
- justify-content
- align-content
- align-item
- rowDividerHeight
- itemDividerWidth

###Example
---
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