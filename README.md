# 使用onlyoffice实现在线编辑、预览例子
绝对五星级的文档在线编辑、协同编辑软件。

## 界面截图
![image](https://github.com/xsi640/onlyoffice_demo/blob/master/1.png)
![image](https://github.com/xsi640/onlyoffice_demo/blob/master/2.png)
![image](https://github.com/xsi640/onlyoffice_demo/blob/master/3.png)
![image](https://github.com/xsi640/onlyoffice_demo/blob/master/4.png)

## 问题
###解决onlyoffice中文乱码问题
删除onlyoffice所有字体，目录在/usr/share/fonts/  
把fonts目录的所有文件cp到/usr/share/fonts  
运行命令重新缓存  
> sudo mkfontscale  
> sudo mkfontdir  
> sudo fc-cache -fv  
> documentserver-generate-allfonts.sh

### 修改20个连接数
修改这个文件  
/var/www/onlyoffice/documentserver/server/Common/sources/constants.js  
这个常量值就是最大连接数 exports.LICENSE_CONNECTIONS  
保存退出后，重启onlyoffice的服务  
> sudo supervisorctl restart onlyoffice-documentserver:docservice  
> sudo supervisorctl restart onlyoffice-documentserver:converter


# 相关连接
https://github.com/ONLYOFFICE/Docker-DocumentServer
