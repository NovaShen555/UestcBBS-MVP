# API Reference (Known)

> 记录当前项目中已知、已使用的 Discourse 相关 API，便于快速查阅。

## Base

- Base URL: https://river-side.cc/

## Auth & Session

| Method | Path | Query | Body | Description |
|---|---|---|---|---|
| GET | session/csrf | - | - | 获取 CSRF Token |
| GET | session/current.json | - | - | 获取当前用户信息 |
| POST (form) | session | - | login, password, second_factor_method, timezone | 登录 |

## Topics

| Method | Path | Query | Description |
|---|---|---|---|
| GET | latest.json | - | 最新回复帖子列表 |
| GET | new.json | - | 最新创建帖子列表 |
| GET | t/{topic_id}.json | - | 帖子详情 |
| GET | topics/created-by/{username}.json | - | 用户创建的帖子 |

## Categories

| Method | Path | Query | Description |
|---|---|---|---|
| GET | categories.json | - | 获取板块分类 |
| GET | c/{category_id}/show.json | - | 板块详情 |

## Users

| Method | Path | Query | Description |
|---|---|---|---|
| GET | u/{username}.json | - | 用户信息 |
| GET | u/{username}/summary.json | - | 用户摘要信息 |
| GET | user_actions.json | offset, username, filter | 用户操作记录 |

## Posts

| Method | Path | Query | Body | Description |
|---|---|---|---|---|
| POST | posts | - | CreatePostRequest(JSON) | 发表评论 |

## Chat Channels

| Method | Path | Query | Description |
|---|---|---|---|
| GET | chat/api/channels | limit, filter, status | 获取频道列表 |

## Chat Messages

| Method | Path | Query | Description |
|---|---|---|---|
| GET | chat/api/channels/{channel_id}/messages | page_size, fetch_from_last_read=true | 获取最新消息（从上次已读开始） |
| GET | chat/api/channels/{channel_id}/messages | page_size, direction=past\|future, target_message_id | 按方向分页加载历史/未来消息 |
| POST | chat/api/channels/{channel_id}/read | message_id | 标记已读 |

## Notes

- `avatar_template` 需替换 `{size}` 并拼接 Base URL：`https://river-side.cc{avatar_template}`。
- Chat 分页：
  - 向上加载历史：`direction=past` + `target_message_id=最早消息ID`
  - 向下加载更新：`direction=future` + `target_message_id=最新消息ID`
