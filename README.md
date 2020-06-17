
# CenterAlignActionBar

----
## 原生的問題
> Android 原生的 ToolBar 是靠左對齊，如果放上icon 很容易有title 超過寬度, 或是蓋過icon的問題.

----
## 我能做到

1. 不被遮蔽
2. 置中對齊
3. 包裹任意元件

----
## 對齊示範 - 自動
|                                                        對齊parent                                                         |                                                                            閃避icon                                                                            |                                                       動態文字                                                       |
|:-----------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------:|
| ![對齊parent](https://github.com/voarlese/CenterAlignTitleActionBar/blob/master/gif/center_parent.gif) | ![閃避icon](https://github.com/voarlese/CenterAlignTitleActionBar/blob/master/gif/center_icon.gif)  | ![動態文字](https://github.com/voarlese/CenterAlignTitleActionBar/blob/master/gif/%E5%8B%95%E6%85%8B%E6%96%87%E5%AD%97.gif) |
|          文字長度預設以左右 parent 對齊                                                         |   若任意一側文字長度在置中於parent時<br>會蓋過icon<br>蓋過icon那側會改以icon 對齊                                                                                                                                          |                                                                                                                     |

----
## 使用元件
* `app:left_component="btnBack"`  輸入放在左側的 viewId，**直接輸入String**
* `app:right_component="btnSearch"` 輸入放在右側的 viewId，**直接輸入String**
* `app:my_title="@id/title"` 輸入 Title viewID，**輸入ID**
```xml
<tw.com.voarlese.centeraligntitleactionbar.TitleCenterAlignActionBar xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tool="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    android:paddingStart="11dp"
    android:paddingEnd="11dp"
    android:background="@color/colorPrimary"
    app:left_component="btnBack"
    app:right_component="btnSearch"
    app:my_title="@id/title">
    <!-- 放入 Title TextView-->
    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="50dp"
        android:ellipsize="end"
        android:gravity="center"
        android:letterSpacing="-0.02"
        android:singleLine="true"
        android:textColor="#FFFFFF"
        android:textSize="17dp"
        android:drawableEnd="@drawable/selector_dropdown"
        tool:text="我是TITLE" />
     <!-- 放入左側 ImageView -->
      <ImageView
        android:id="@+id/btnBack"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:paddingStart="5dp"
        android:paddingEnd="5dp"
        android:scaleType="centerInside"
        android:src="@drawable/ic_keyboard_arrow_left_white_24dp"
        android:visibility="gone" />
     <!-- 放入右側 ImageView -->
     <ImageView
        android:id="@+id/btnSearch"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:paddingStart="5dp"
        android:paddingEnd="5dp"
        android:scaleType="centerInside"
        android:src="@drawable/ic_search_white_24dp"
        android:visibility="gone" />
    </tw.com.voarlese.centeraligntitleactionbar.TitleCenterAlignActionBar>
```

---
## 切換顯示隱藏
### 顯示
```java
TitleCenterAlignActionBar actionBar = findViewById(R.id.actionBar);
View btnBack = actionBar.findViewById(R.id.btnBack);
btnBack.setvisible(View.Visible)
```
### 隱藏
```java
TitleCenterAlignActionBar actionBar = findViewById(R.id.actionBar);
View btnBack = actionBar.findViewById(R.id.btnBack);
btnBack.setvisible(View.GONE)
```