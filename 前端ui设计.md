数据库对象：

用户表：
用户id，user_id
用户名username
用户账号name
密码password
加密后密码token
点赞的文章user_like_article
粉丝fans
关注focus
头像photo

文章表：
文章id，article_id
作者id，user_id
文章标题title
文章内容content
状态state-----0,1,2,3，对应草稿(0)，我们(1)，游记(2)，计划(3)
创建时间create_time
更新时间update_time
浏览数量view_count
点赞数量like_count

图片表：
链接url

评论表：
文章id，article_id
评论作者id，suser_id
评论作者用户名username
评论内容content
创建时间create_time
更新时间update_time
点赞数量like_count