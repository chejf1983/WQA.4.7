REM 声明采用UTF-8编码
chcp 65001

del .\*.jar

@echo off
echo copy h2数据库开发包
copy ..\..\..\lib\h2\bin\h2-1.3.173.jar .\  
echo copy excl开发包
copy ..\..\..\lib\jexcelapi\jxl.jar .\
echo copy jyloo皮肤开发包
copy ..\..\..\lib\jyloo\synthetica_2.17.1_eval\synthetica.jar .\
echo copy 自定义jyloo皮肤开发包
copy ..\..\..\myjyloo\syntheticaAluOxide0\syntheticaAluOxide.jar .\

echo copy jfreechart图表开发包
copy ..\..\..\lib\jfreechart-1.0.19\lib\jcommon-1.0.23.jar .\
copy ..\..\..\lib\jfreechart-1.0.19\lib\jfreechart-1.0.19.jar .\
copy ..\..\..\lib\jfreechart-1.0.19\lib\jfreechart-1.0.19-swt.jar .\

echo copy 自定义平台开发包
copy ..\..\..\PlatForm\commonbean\dist\commonbean.jar .\
copy ..\..\..\PlatForm\NahonDrv\dev_migp_base\dist\dev_migp_base.jar .\
copy ..\..\..\PlatForm\WindowsIOdrv\windows_io_driver\store\windows_io_driver.jar .\

pause