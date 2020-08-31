-----------------------------------------
--version[1.0.0], DO NOT EDIT THIS LINE!
-----------------------------------------

CREATE TABLE DATAGEAR_VERSION
(
	VERSION_MAJOR VARCHAR(50),
	VERSION_MINOR VARCHAR(50),
	VERSION_REVISION VARCHAR(50),
	VERSION_BUILD VARCHAR(50)
);

CREATE TABLE DATAGEAR_USER
(
	USER_ID VARCHAR(50) NOT NULL,
	USER_NAME VARCHAR(50) NOT NULL,
	USER_PASSWORD VARCHAR(200) NOT NULL,
	USER_REAL_NAME VARCHAR(100),
	USER_EMAIL VARCHAR(200),
	USER_IS_ADMIN VARCHAR(20),
	USER_CREATE_TIME TIMESTAMP,
	PRIMARY KEY (USER_ID),
	UNIQUE (USER_NAME)
);

--the password is 'admin'
INSERT INTO DATAGEAR_USER VALUES('admin', 'admin', '4c6d8d058a4db956660f0ee51fcb515f93471a086fc676bfb71ba2ceece5bf4702c61cefab3fa54b', '', '', 'true', CURRENT_TIMESTAMP);

CREATE TABLE DATAGEAR_SCHEMA
(
	SCHEMA_ID VARCHAR(50) NOT NULL,
	SCHEMA_TITLE VARCHAR(100) NOT NULL,
	SCHEMA_URL VARCHAR(200) NOT NULL,
	SCHEMA_USER VARCHAR(200),
	SCHEMA_PASSWORD VARCHAR(200),
	SCHEMA_CREATE_USER_ID VARCHAR(50),
	SCHEMA_CREATE_TIME TIMESTAMP,
	SCHEMA_SHARED VARCHAR(20),
	DRIVER_ENTITY_ID VARCHAR(100),
	PRIMARY KEY (SCHEMA_ID)
);


-----------------------------------------
--version[1.1.0], DO NOT EDIT THIS LINE!
-----------------------------------------



-----------------------------------------
--version[1.1.1], DO NOT EDIT THIS LINE!
-----------------------------------------



-----------------------------------------
--version[1.2.0], DO NOT EDIT THIS LINE!
-----------------------------------------



-----------------------------------------
--version[1.3.0], DO NOT EDIT THIS LINE!
-----------------------------------------



-----------------------------------------
--version[1.4.0], DO NOT EDIT THIS LINE!
-----------------------------------------

CREATE TABLE DATAGEAR_ROLE
(
	ROLE_ID VARCHAR(50) NOT NULL,
	ROLE_NAME VARCHAR(100) NOT NULL,
	ROLE_DESCRIPTION VARCHAR(200),
	ROLE_ENABLED VARCHAR(10) NOT NULL,
	ROLE_CREATE_TIME TIMESTAMP,
	PRIMARY KEY (ROLE_ID)
);

CREATE TABLE DATAGEAR_ROLE_USER
(
	RU_ID VARCHAR(50) NOT NULL,
	RU_ROLE_ID VARCHAR(50) NOT NULL,
	RU_USER_ID VARCHAR(50) NOT NULL,
	PRIMARY KEY (RU_ID)
);

ALTER TABLE DATAGEAR_ROLE_USER ADD FOREIGN KEY (RU_ROLE_ID) REFERENCES DATAGEAR_ROLE (ROLE_ID) ON DELETE CASCADE;

ALTER TABLE DATAGEAR_ROLE_USER ADD FOREIGN KEY (RU_USER_ID) REFERENCES DATAGEAR_USER (USER_ID) ON DELETE CASCADE;

ALTER TABLE DATAGEAR_ROLE_USER ADD CONSTRAINT UK_RU_ROLE_USER_ID UNIQUE (RU_ROLE_ID, RU_USER_ID);

CREATE TABLE DATAGEAR_AUTHORIZATION
(
	AUTH_ID VARCHAR(50) NOT NULL,
	AUTH_RESOURCE VARCHAR(200) NOT NULL,
	AUTH_RESOURCE_TYPE VARCHAR(50) NOT NULL,
	AUTH_PRINCIPAL VARCHAR(200) NOT NULL,
	AUTH_PRINCIPAL_TYPE VARCHAR(50) NOT NULL,
	AUTH_PERMISSION SMALLINT NOT NULL,
	AUTH_ENABLED VARCHAR(10) NOT NULL,
	AUTH_CREATE_TIME TIMESTAMP,
	AUTH_CREATE_USER_ID VARCHAR(50),
	PRIMARY KEY (AUTH_ID)
);

--自定义REPLACE函数
CREATE FUNCTION DATAGEAR_REPLACE(orgStr VARCHAR(500), oldStr VARCHAR(100), newStr VARCHAR(100)) RETURNS VARCHAR(500)
PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'org.datagear.management.util.DerbyFunctionSupport.replace';

CREATE TABLE DATAGEAR_SQL_HISTORY
(
	SQLHIS_ID VARCHAR(50) NOT NULL,
	SQLHIS_SQL VARCHAR(5000) NOT NULL,
	SQLHIS_SCHEMA_ID VARCHAR(50) NOT NULL,
	SQLHIS_USER_ID VARCHAR(50) NOT NULL,
	SQLHIS_CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (SQLHIS_ID)
);

ALTER TABLE DATAGEAR_SQL_HISTORY ADD FOREIGN KEY (SQLHIS_SCHEMA_ID) REFERENCES DATAGEAR_SCHEMA (SCHEMA_ID) ON DELETE CASCADE;


-----------------------------------------
--version[1.5.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--SQL数据集
CREATE TABLE DATAGEAR_SQL_DATA_SET
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_NAME VARCHAR(100) NOT NULL,
	DS_SCHEMA_ID VARCHAR(50) NOT NULL,
	DS_SQL VARCHAR(1000) NOT NULL,
	DS_CREATE_USER_ID VARCHAR(50),
	DS_CREATE_TIME TIMESTAMP,
	PRIMARY KEY (DS_ID)
);

ALTER TABLE DATAGEAR_SQL_DATA_SET ADD FOREIGN KEY (DS_SCHEMA_ID) REFERENCES DATAGEAR_SCHEMA (SCHEMA_ID);

--数据集属性
CREATE TABLE DATAGEAR_DATA_SET_PROP
(
	PROP_DS_ID VARCHAR(50) NOT NULL,
	PROP_NAME VARCHAR(100) NOT NULL,
	PROP_TYPE VARCHAR(50) NOT NULL,
	PROP_LABEL VARCHAR(100),
	PROP_ORDER INTEGER
);

ALTER TABLE DATAGEAR_DATA_SET_PROP ADD FOREIGN KEY (PROP_DS_ID) REFERENCES DATAGEAR_SQL_DATA_SET (DS_ID) ON DELETE CASCADE;

ALTER TABLE DATAGEAR_DATA_SET_PROP ADD CONSTRAINT UK_DS_PROP_DS_ID_NAME UNIQUE (PROP_DS_ID, PROP_NAME);

--数据集参数
CREATE TABLE DATAGEAR_DATA_SET_PAR
(
	PAR_DS_ID VARCHAR(50) NOT NULL,
	PAR_NAME VARCHAR(100) NOT NULL,
	PAR_TYPE VARCHAR(100) NOT NULL,
	PAR_REQUIRED VARCHAR(10),
	PAR_DEFAULT_VALUE VARCHAR(200),
	PAR_ORDER INTEGER
);

ALTER TABLE DATAGEAR_DATA_SET_PAR ADD FOREIGN KEY (PAR_DS_ID) REFERENCES DATAGEAR_SQL_DATA_SET (DS_ID) ON DELETE CASCADE;

ALTER TABLE DATAGEAR_DATA_SET_PAR ADD CONSTRAINT UK_DS_PAR_DS_ID_NAME UNIQUE (PAR_DS_ID, PAR_NAME);

--数据集输出
CREATE TABLE DATAGEAR_DATA_SET_EXPT
(
	EXPT_DS_ID VARCHAR(50) NOT NULL,
	EXPT_NAME VARCHAR(100) NOT NULL,
	EXPT_TYPE VARCHAR(50) NOT NULL,
	EXPT_ORDER INTEGER
);

ALTER TABLE DATAGEAR_DATA_SET_EXPT ADD FOREIGN KEY (EXPT_DS_ID) REFERENCES DATAGEAR_SQL_DATA_SET (DS_ID) ON DELETE CASCADE;

ALTER TABLE DATAGEAR_DATA_SET_EXPT ADD CONSTRAINT UK_DS_EXPT_DS_ID_NAME UNIQUE (EXPT_DS_ID, EXPT_NAME);

--图表
CREATE TABLE DATAGEAR_HTML_CHART_WIDGET
(
	HCW_ID VARCHAR(50) NOT NULL,
	HCW_NAME VARCHAR(100) NOT NULL,
	HCW_PLUGIN_ID VARCHAR(100) NOT NULL,
	HCW_UPDATE_INTERVAL INTEGER,
	HCW_CREATE_USER_ID VARCHAR(50),
	HCW_CREATE_TIME TIMESTAMP,
	PRIMARY KEY (HCW_ID)
);

--图表-数据集信息
CREATE TABLE DATAGEAR_HCW_DS
(
	HCW_ID VARCHAR(50) NOT NULL,
	DS_ID VARCHAR(50) NOT NULL,
	DS_PROPERTY_SIGNS VARCHAR(500),
	DS_ORDER INTEGER
);

ALTER TABLE DATAGEAR_HCW_DS ADD FOREIGN KEY (HCW_ID) REFERENCES DATAGEAR_HTML_CHART_WIDGET (HCW_ID) ON DELETE CASCADE;

ALTER TABLE DATAGEAR_HCW_DS ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_SQL_DATA_SET (DS_ID);

--看板
CREATE TABLE DATAGEAR_HTML_DASHBOARD
(
	HD_ID VARCHAR(50) NOT NULL,
	HD_NAME VARCHAR(100) NOT NULL,
	HD_TEMPLATE VARCHAR(100) NOT NULL,
	HD_TEMPLATE_ENCODING VARCHAR(50),
	HD_CREATE_USER_ID VARCHAR(50),
	HD_CREATE_TIME TIMESTAMP,
	PRIMARY KEY (HD_ID)
);


-----------------------------------------
--version[1.6.0], DO NOT EDIT THIS LINE!
-----------------------------------------

ALTER TABLE DATAGEAR_HTML_DASHBOARD ALTER COLUMN HD_TEMPLATE SET DATA TYPE VARCHAR(500);

-----------------------------------------
--version[1.6.1], DO NOT EDIT THIS LINE!
-----------------------------------------

-----------------------------------------
--version[1.7.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--自定义正则REPLACE函数，目前仅用于下面修改数据标记
CREATE FUNCTION DATAGEAR_REPLACEREGEX(orgStr VARCHAR(500), oldStr VARCHAR(100), newStr VARCHAR(100)) RETURNS VARCHAR(500)
PARAMETER STYLE JAVA NO SQL LANGUAGE JAVA EXTERNAL NAME 'org.datagear.management.util.DerbyFunctionSupport.replaceRegex';

--修改折线图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*xvalue[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*yvalue[^\]]*"\]', '["value"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line');

--修改柱状图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*xvalue[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.bar');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*yvalue[^\]]*"\]', '["value"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.bar');

--修改饼图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*name[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.pie');

--修改散点图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*xvalue[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*yvalue[^\]]*"\]', '["value"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter');

--修改雷达图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*dataName[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*dataValue[^\]]*"\]', '["value"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*dataMax[^\]]*"\]', '["max"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar');

--修改漏斗图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*name[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.funnel');

--修改地图数据标记
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*name[^\]]*"\]', '["coord"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.map');

--扩充SQL字段
ALTER TABLE DATAGEAR_SQL_DATA_SET ALTER COLUMN DS_SQL SET DATA TYPE VARCHAR(10000);


-----------------------------------------
--version[1.8.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--默认值字段改为描述字段
RENAME COLUMN DATAGEAR_DATA_SET_PAR.PAR_DEFAULT_VALUE TO PAR_DESC;

--移除数据集输出项表
DROP TABLE DATAGEAR_DATA_SET_EXPT;

--扩充图表数据集数据标记列
ALTER TABLE DATAGEAR_HCW_DS ALTER COLUMN DS_PROPERTY_SIGNS SET DATA TYPE VARCHAR(1000);

--添加图表数据集别名列
ALTER TABLE DATAGEAR_HCW_DS ADD COLUMN DS_ALIAS VARCHAR(100);

--添加图表数据集参数值列
ALTER TABLE DATAGEAR_HCW_DS ADD COLUMN DS_PARAM_VALUES VARCHAR(1000);


-----------------------------------------
--version[1.8.1], DO NOT EDIT THIS LINE!
-----------------------------------------



-----------------------------------------
--version[1.9.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--规范所有图表插件的数据标记名
UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line.area');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line.area.smooth');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line.area.smooth.stack');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line.area.stack');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.line.smooth');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.bar');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.bar.horizontal');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.bar.stack');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.bar.stack.horizontal');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.pie');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.pie.ring');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.pie.rose');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord2[^\]]*"\]', '["2drooc"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter.coord');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter.coord');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*value[^\]]*"\]', '["weight"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter.coord');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*2drooc[^\]]*"\]', '["value"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.scatter.coord');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*name[^\]]*"\]', '["item"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*name[^\]]*"\]', '["item"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar.circle');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.radar.circle');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.funnel');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.funnel.pyramid');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.map');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coordLongitude[^\]]*"\]', '["longitude"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.map.scatter');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coordLatitude[^\]]*"\]', '["latitude"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.map.scatter');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.map.scatter');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.candlestick');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord2[^\]]*"\]', '["2drooc"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.heatmap');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.heatmap');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*value[^\]]*"\]', '["weight"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.heatmap');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*2drooc[^\]]*"\]', '["value"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.heatmap');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.label');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.label.hideName');

UPDATE DATAGEAR_HCW_DS SET DS_PROPERTY_SIGNS=DATAGEAR_REPLACEREGEX(DS_PROPERTY_SIGNS, '\["[^\]]*coord[^\]]*"\]', '["name"]')
WHERE HCW_ID IN (SELECT HCW_ID FROM DATAGEAR_HTML_CHART_WIDGET WHERE HCW_PLUGIN_ID='org.datagear.chart.label.valueFirst');


-----------------------------------------
--version[1.10.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--添加数据集参数输入框类型
ALTER TABLE DATAGEAR_DATA_SET_PAR ADD COLUMN PAR_INPUT_TYPE VARCHAR(50);

--添加数据集参数输入框载荷
ALTER TABLE DATAGEAR_DATA_SET_PAR ADD COLUMN PAR_INPUT_PAYLOAD VARCHAR(1000);


-----------------------------------------
--version[1.10.1], DO NOT EDIT THIS LINE!
-----------------------------------------

-----------------------------------------
--version[1.11.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--改为通用数据集
RENAME TABLE DATAGEAR_SQL_DATA_SET TO DATAGEAR_DATA_SET;

--新建SQL数据集
CREATE TABLE DATAGEAR_DATA_SET_SQL
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_SCHEMA_ID VARCHAR(50) NOT NULL,
	DS_SQL VARCHAR(10000) NOT NULL,
	PRIMARY KEY (DS_ID)
);

--迁移SQL数据集数据
INSERT INTO DATAGEAR_DATA_SET_SQL (DS_ID, DS_SCHEMA_ID, DS_SQL) SELECT DS_ID, DS_SCHEMA_ID, DS_SQL FROM DATAGEAR_DATA_SET;

--添加约束
ALTER TABLE DATAGEAR_DATA_SET_SQL ADD FOREIGN KEY (DS_SCHEMA_ID) REFERENCES DATAGEAR_SCHEMA (SCHEMA_ID);

ALTER TABLE DATAGEAR_DATA_SET_SQL ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_DATA_SET (DS_ID) ON DELETE CASCADE;

--移除通用数据集中的SQL数据集列
ALTER TABLE DATAGEAR_DATA_SET DROP COLUMN DS_SCHEMA_ID;

ALTER TABLE DATAGEAR_DATA_SET DROP COLUMN DS_SQL;

--为通用数据集添加类型字段，并全部初始化为'SQL'
ALTER TABLE DATAGEAR_DATA_SET ADD COLUMN DS_TYPE VARCHAR(50);

UPDATE DATAGEAR_DATA_SET SET DS_TYPE = 'SQL';

ALTER TABLE DATAGEAR_DATA_SET ALTER COLUMN DS_TYPE SET NOT NULL;

--2020-08-10
--JSON值数据集
CREATE TABLE DATAGEAR_DATA_SET_JSON_VALUE
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_VALUE VARCHAR(10000) NOT NULL,
	PRIMARY KEY (DS_ID)
);

ALTER TABLE DATAGEAR_DATA_SET_JSON_VALUE ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_DATA_SET (DS_ID) ON DELETE CASCADE;

--2020-08-12
--JSON文件数据集
CREATE TABLE DATAGEAR_DATA_SET_JSON_FILE
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_FILE_NAME VARCHAR(100) NOT NULL,
	DS_DISPLAY_NAME VARCHAR(100) NOT NULL,
	PRIMARY KEY (DS_ID)
);

ALTER TABLE DATAGEAR_DATA_SET_JSON_FILE ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_DATA_SET (DS_ID) ON DELETE CASCADE;


-----------------------------------------
--version[1.11.1], DO NOT EDIT THIS LINE!
-----------------------------------------


-----------------------------------------
--version[1.12.0], DO NOT EDIT THIS LINE!
-----------------------------------------

--2020-08-28
--JSON文件数据集添加编码字段
ALTER TABLE DATAGEAR_DATA_SET_JSON_FILE ADD COLUMN DS_FILE_ENCODING VARCHAR(50);

--2020-08-28
--Excel文件数据集
CREATE TABLE DATAGEAR_DATA_SET_EXCEL
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_FILE_NAME VARCHAR(100) NOT NULL,
	DS_DISPLAY_NAME VARCHAR(100) NOT NULL,
	DS_SHEET_INDEX INTEGER,
	DS_NAME_ROW INTEGER,
	DS_DATA_ROW_EXP VARCHAR(100),
	DS_DATA_COLUMN_EXP VARCHAR(100),
	DS_FORCE_XLS VARCHAR(10),
	PRIMARY KEY (DS_ID)
);

ALTER TABLE DATAGEAR_DATA_SET_EXCEL ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_DATA_SET (DS_ID) ON DELETE CASCADE;

--2020-08-31
--CSV值数据集
CREATE TABLE DATAGEAR_DATA_SET_CSV_VALUE
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_VALUE VARCHAR(10000) NOT NULL,
	DS_NAME_ROW INTEGER,
	PRIMARY KEY (DS_ID)
);

ALTER TABLE DATAGEAR_DATA_SET_CSV_VALUE ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_DATA_SET (DS_ID) ON DELETE CASCADE;

--2020-08-31
--CSV文件数据集
CREATE TABLE DATAGEAR_DATA_SET_CSV_FILE
(
	DS_ID VARCHAR(50) NOT NULL,
	DS_FILE_NAME VARCHAR(100) NOT NULL,
	DS_DISPLAY_NAME VARCHAR(100) NOT NULL,
	DS_FILE_ENCODING VARCHAR(50),
	DS_NAME_ROW INTEGER,
	PRIMARY KEY (DS_ID)
);

ALTER TABLE DATAGEAR_DATA_SET_CSV_FILE ADD FOREIGN KEY (DS_ID) REFERENCES DATAGEAR_DATA_SET (DS_ID) ON DELETE CASCADE;
