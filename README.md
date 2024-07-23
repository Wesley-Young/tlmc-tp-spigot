# tlmc-tp-spigot

支持基岩版 GUI 的传送插件。

## 依赖
- floodgate

如果使用代理端，请确保代理端和下游服务端上都安装了 floodgate，并且在代理端的 floodgate 的 `config.yml` 中启用了 `send-floodgate-data`，并且保持双端的 `key.pem` 一致。请参照[这里](https://geysermc.org/wiki/floodgate/setup/?platform=proxy-servers)进行设置。

## 命令
- `/tpa [玩家名]` - 向某位玩家发出传送请求
- `/tpaccept` - 同意传送请求
- `/tpdeny` - 拒绝传送请求
- `/warp [公共传送点名称]` - 传送至某公共传送点
- `/setwarp [nature|machine|structure] [公共传送点名称, 建议使用中文] [描述]` - 添加公共传送点，仅限管理使用
- `/delwarp [公共传送点名称]` - 删除公共传送点，仅限管理使用
- `/home [个人传送点名称]` - 传送至某个人传送点（家）
- `/setwarp [个人传送点名称]` - 添加个人传送点，最多 16 个
- `/delwarp [个人传送点名称]` - 删除个人传送点

以下命令仅限基岩版使用：
- `/snowball` - 获得一个“魔法雪球”，扔出后可以打开菜单，包含上述所有功能