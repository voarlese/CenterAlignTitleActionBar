
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
|                                                        閃避icon                                                         |                                                                            對齊parent                                                                            |                                                       動態文字                                                       |
|:-----------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------:|
| ![閃避icon](https://github.com/voarlese/CenterAlignTitleActionBar/blob/master/gif/center_parent.gif) | ![對齊parent](https://github.com/voarlese/CenterAlignTitleActionBar/blob/master/gif/center_icon.gif) | ![動態文字](https://github.com/voarlese/CenterAlignTitleActionBar/blob/master/gif/%E5%8B%95%E6%85%8B%E6%96%87%E5%AD%97.gif) |
|          文字長度預設以左右 parent 對齊                                                         |   若任意一側文字長度在置中於parent時<br>會蓋過icon<br>蓋過icon那側會改以icon 對齊                                                                                                                                          |                                                                                                                     |


