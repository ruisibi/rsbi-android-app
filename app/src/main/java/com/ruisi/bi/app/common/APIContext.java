package com.ruisi.bi.app.common;

/**
 * 该类提供服务器端接口
 */
public class APIContext {

    /** POST请求方式 */
    public static final String POST = "POST";

    /** SOAP请求方式 */
    public static final String SOAP = "SOAP";

    /** GET请求方式 */
    public static final String GET = "GET";

    /** ======================接口基地�?=============================== */

//    public static  String HOST = "http://112.124.13.251:8081/bi/";
    public static String HOST = "https://www.ruisitech.com/cloud";

    public static final String Login="app/Login!login.action";
    public static final String Menu="app/Menus!topMenu.action";
    public static final String Menu2="app/Menus!topMenu2.action";
    public static final String Theme="app/Subject!list.action";
    public static final String Zhibiao="app/Cube!getKpi.action";
    public static final String Weidu="app/Cube!getDim.action";
    public static final String form="app/CompView!viewTable.action";
    public static final String tu="app/CompView!viewChart.action";
    public static final String shaixuan="app/DimFilter.action";
    public static final String baobiaomulu="app/Report!listCata.action";
    public static final String baobiaoItem="app/Report!listReport.action";
    public static final String fenxisave="app/Usave!save.action";
    public static final String fenxiupdata="app/Usave!update.action";
    public static final String getfenxiupdata="app/Usave!get.action";
    public static final String deletefenxiupdata="app/Usave!delete.action";
    public static final String getfenxiupdatalist="app/Usave!list.action";
    public static final String userMessage="app/UInfo.action";
    public static final String logout="app/Login!logout.action";
    public static final String addCollection="app/Collect!add.action";
    public static final String delCollection="app/Collect!delete.action";
    public static final String getCollection="app/Collect!list.action";

    public static final String getPushList = "app/Push!list.action";
    public static final String getPushSubjectList = "app/Push!listPushSubject.action";
    public static final String savePush = "app/Push!save.action";
    public static final String updatePush = "app/Push!update.action";
    public static final String delPush = "app/Push!del.action";
    public static final String getPush = "app/Push!get.action";

    public static final String getMsgList = "app/Push!listMsg.action";
    public static final String getMsgList2 = "app/Push!listMsg2.action";
    public static final String getMsg = "app/Push!getMsg.action";
    public static final String msg2Read = "app/Push!msg2Read.action";
    public static final String delMsg = "app/Push!delMsg.action";
    public static final String delAllMsg = "app/Push!delAll.action";

    public static final String stopPush = "app/Push!stopPush.action";
    public static final String startPush = "app/Push!startPush.action";
    public static final String updateChennel = "app/Push!updateChennel.action";

    public static final int most_show_count=12;
    public static final String filepath = "http://blog.csdn.net/lihonghao1017/article/details/50560323";
    
    
    public static int ThemeId,id;
    public static String selectData;
    public static boolean isFromSave;
    
}
