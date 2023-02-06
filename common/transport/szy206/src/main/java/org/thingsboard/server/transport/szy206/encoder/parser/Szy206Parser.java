package org.thingsboard.server.transport.szy206.encoder.parser;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.transport.szy206.encoder.constant.DataInfoConstant;
import org.thingsboard.server.transport.szy206.encoder.dto.Szy206InfoDTO;
import org.thingsboard.server.transport.szy206.encoder.emums.Szy206TypeEnum;
import org.thingsboard.server.transport.szy206.encoder.emums.Szy206WaterQualityEnum;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Szy206Parser {

    public static Szy206InfoDTO dataParser(String msg) {
        Szy206InfoDTO szy206InfoDTO=new Szy206InfoDTO();
        //示例 681A68B35303810D00C00059000000826110000001106000004508090A3516

        //681A68开始字符串 字符串长度 开始字符串
        Integer index=0;
        //开始字符串
        String startStr=msg.substring(index,index+6);
        szy206InfoDTO.setStartStr(startStr);
        index+=6;


        //B3为数据类型（流量（水量））
        //1) 0000B—雨量；
        //2) 0001B—水位；
        //3) 0010B—流量（水量）；
        //4) 0011B—流速；
        //5) 0100B—闸位；
        //6) 0101B—功率；
        //7) 0110B—气压；
        //8) 0111B—风速（风向）；
        //9) 1000B—水温；
        //10) 1001B—水质；
        //11) 1010B—土壤含水率；
        //12) 1011B—水压；
        //13) 1100B—1111B 备用。

        //数据类型
        String dataType=msg.substring(index,index+2);
        szy206InfoDTO.setDataType(dataType);
        index+=2;

        //5303810D00为测站地址  530381为行政区代码  0D00测站地址
        //遥测地址
        String stationAddr=msg.substring(index,index+10);
        szy206InfoDTO.setStationAddr(stationAddr);
        szy206InfoDTO.setDeviceId(stationAddr);
        index+=10;
        //C0   C0H遥测终端自报实时数据 遥测终端 中心站
        //应用层功能码
        String functionCode=msg.substring(index,index+2);
        szy206InfoDTO.setFunctionCode(functionCode);
        index+=2;
        //0059000000826110000001106000004508090A  报文正文
        //正文
        String content=msg.substring(index,msg.length()-4);
        szy206InfoDTO.setContent(content);
        index=msg.length()-4;
        //35 crc校验
        //crc校验码
        String crcCode=msg.substring(index,index+2);
        szy206InfoDTO.setCrcCode(crcCode);
        index+=2;
        //16 结束字符(固定)
        //结束字符串16
        String endStr=msg.substring(index,index+2);
        szy206InfoDTO.setEndStr(endStr);

        parseContent(content,szy206InfoDTO);
//        Map<String,Object> map= (Map<String, Object>) JSONUtil.parse(szy206InfoDTO);
        return szy206InfoDTO;

    }

    private static void parseContent(String content, Szy206InfoDTO szy206InfoDTO) {
        Map<String,Object> mainBody=new HashMap<>();
        Map<String,Object> mainHead=new HashMap<>();
        //倒着解析
        int index=content.length();
        if(index>18){
            //004508090A
            //00（秒-十位个位）45（分-十位个位）08（时-十位个位）09（日-十位个位），解析后为09日 08:45:00 年月需要自己补充。
            String time=content.substring(index-10,index-2);
            Integer year= DateTime.now().year();
            Integer month=DateTime.now().monthBaseOne();
            Integer day=Integer.parseInt(time.substring(6,8));
            Integer hour=Integer.parseInt(time.substring(4,6));
            Integer minutes=Integer.parseInt(time.substring(2,4));
            Integer seconds=Integer.parseInt(time.substring(0,2));
            String dateTimeStr=year+"/"+month+"/"+day+" "+hour+":"+minutes+":"+seconds;
            DateTime dateTime= DateUtil.parseDateTime(dateTimeStr);
            String timeStr= dateTime.toString();
            szy206InfoDTO.setDataTime(timeStr);

            mainBody.put(DataInfoConstant.DATA_TIME,timeStr);
            mainHead.put(DataInfoConstant.DATA_TIME,DataInfoConstant.DATA_TIME_MEAN);
            index-=10;
            String warningStatus=content.substring(index-8,index);
            index-=8;
            String dataContent=content.substring(0,index);
            if(Szy206TypeEnum.WATER_LEVEL.getKey().equals(szy206InfoDTO.getDataType())){
                //水位

                //每N个数组 代表一个数据
                int oneDataLength=8;
                //小数点位数
                int decimalPoint=5;

                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,true,mainBody,mainHead);
            }else if(Szy206TypeEnum.TRAFFIC.getKey().equals(szy206InfoDTO.getDataType())){
                //流量

                //每N个数组 代表一个数据
                int oneDataLength=10;
                //小数点位数
                int decimalPoint=7;
                int dataLength=dataContent.length();
                if(dataLength%oneDataLength==0) {
                    int cycleNum = 0;
                    int flag=2;
                    while (dataLength > 0) {
                        try {
                            String data = dataContent.substring(cycleNum * oneDataLength, (cycleNum + 1) * oneDataLength);
                            cycleNum++;
                            dataLength -= oneDataLength;
                            char[] dataChar = data.toCharArray();
                            String result;
                            //实时流量在前 累计流量在后
                            if(flag%2==0){
                                //流量实时数据
                                result = char2String(dataChar, decimalPoint,true);
                                Double resultData=Double.parseDouble(result);
                                mainBody.put("flow",resultData);
                                mainHead.put("flow","流量");
                                log.info("遥测地址："+szy206InfoDTO.getStationAddr()+" 类型：流量"+" 结果："+resultData);
                            }else{
                                //累计流量(水量)
                                result = char2String(dataChar, 0,true);
                                Double resultData=Double.parseDouble(result);
                                mainBody.put("countFlow",resultData);
                                mainHead.put("countFlow","累计流量");
                                log.info("遥测地址："+szy206InfoDTO.getStationAddr()+" 类型：累计流量"+" 结果："+resultData);
                            }
                            flag++;


                        } catch (Exception e) {
                            log.error("解析流量（水量）异常", e);
                        }
                    }
                }
            }else if(Szy206TypeEnum.VELOCITY.getKey().equals(szy206InfoDTO.getDataType())){
                //流速

                //每N个数组 代表一个数据
                int oneDataLength=6;
                //小数点位数
                int decimalPoint=3;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,true,mainBody,mainHead);
            }else if(Szy206TypeEnum.GATES.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=6;
                //小数点位数
                int decimalPoint=4;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.POWER.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=6;
                //小数点位数
                int decimalPoint=0;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.AIR_PRESSURE.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=6;
                //小数点位数
                int decimalPoint=0;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.WIND_SPEED.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=6;
                //小数点位数
                int decimalPoint=4;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.WATER_TEMPERATURE.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=4;
                //小数点位数
                int decimalPoint=3;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.MOISTURE_CONTENT.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=4;
                //小数点位数
                int decimalPoint=4;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.WATER_PRESSURE.getKey().equals(szy206InfoDTO.getDataType())){
                //每N个数组 代表一个数据
                int oneDataLength=8;
                //小数点位数
                int decimalPoint=7;
                parentDataInfo(szy206InfoDTO,dataContent,oneDataLength,decimalPoint,false,mainBody,mainHead);
            }else if(Szy206TypeEnum.WATER_QUALITY.getKey().equals(szy206InfoDTO.getDataType())){
                //水质
                //每N个数组 代表一个数据
                int oneDataLength=8;
                //前十位是检测种类
                String typeStr=dataContent.substring(0,10);
                //每位转成4位二进制数 前面补0
                char[] typeChar=typeStr.toCharArray();
                String binStr="";
                for(char ch:typeChar){
                    binStr+=hexString2binaryString(new StringBuilder(ch).toString());
                }
                //更加2进制字符串判断 存在哪些水质参数 1代表存在。
                char[] binaryTypeChar=binStr.toCharArray();
                //存在粪大肠菌群的话 每5个字符代表一个数值 否则4个   （5＋N*4（＋1【参数含粪大肠菌群时】）+4 个字节。）
                if(new StringBuilder(binaryTypeChar[24]).toString().equals("1")){
                    oneDataLength=10;
                }
                List<Integer> indexList=new ArrayList<>();
                //记录1的位置
                for(int i=0;i<binaryTypeChar.length;i++){
                    if(new StringBuilder(binaryTypeChar[i]).toString().equals("1")){
                        indexList.add(i);
                    }
                }

                //获取所有数据list
                int dataLength=dataContent.length();
                List<String> dataList=new ArrayList<>();
                if(dataLength%oneDataLength==0) {
                    int cycleNum=0;
                    while (dataLength > 0) {
                        String data = dataContent.substring(cycleNum * oneDataLength, (cycleNum + 1) * oneDataLength);
                        dataList.add(data);
                    }
                }

                if(indexList.size()==dataList.size()){
                   for(int i=0;i<indexList.size();i++){
                       Szy206WaterQualityEnum szy206WaterQualityEnum =getQualityEnumByKey("D"+indexList.get(i));
                       if(ObjectUtil.isNotEmpty(szy206WaterQualityEnum)){
                           //N(3,1) oneDataLength-3+(3-1)  ==oneDataLength-1; 00000123 小数位数为7
                           Integer decimalPoint=oneDataLength-Integer.parseInt(szy206WaterQualityEnum.getPrecision().split(",")[1].replace(")",""));
                           //TODO不确定是直接将数字倒序就行还是 以下方式（待验证）
                           Double value=Double.parseDouble(char2String(dataList.get(i).toCharArray(),decimalPoint,false));
                           String key=szy206WaterQualityEnum.getKey();
                           log.info("遥测地址："+szy206InfoDTO.getStationAddr()+"类型："+szy206WaterQualityEnum.getName()+"结果："+value);
                           mainBody.put(key,value);
                           mainHead.put(key,szy206WaterQualityEnum.getName()+"("+szy206WaterQualityEnum.getUnit()+")");
                       }
                   }
                }
            }

        }
        szy206InfoDTO.setMainBody(mainBody);
        szy206InfoDTO.setMainHead(mainHead);
    }

    public static Szy206WaterQualityEnum getQualityEnumByKey(String key){
        for(Szy206WaterQualityEnum e:Szy206WaterQualityEnum.values()){
            if(e.getKey().equals(key)){
                return e;
            }
        }
        return null;
    }

    /**
     * 解析正文
     * @param szy206InfoDTO 解析实体
     * @param dataContent 正文
     * @param oneDataLength 一个数据长度
     * @param decimalPoint 小数位数
     * @param isNegative 是否判断正方
     * @param mainBody 数据
     * @param mainHead 头部
     */
    public static void parentDataInfo( Szy206InfoDTO szy206InfoDTO,String dataContent,Integer oneDataLength, int decimalPoint,Boolean isNegative,Map<String,Object> mainBody,Map<String,Object> mainHead){

        int dataLength=dataContent.length();
        if(dataLength%oneDataLength==0){
            int cycleNum=0;
            while (dataLength>0){
                try{
                    String data=dataContent.substring(cycleNum*oneDataLength,(cycleNum+1)*oneDataLength);
                    cycleNum++;
                    dataLength-=oneDataLength;
                    char[] dataChar=data.toCharArray();
                    String result=char2String(dataChar,decimalPoint,isNegative);
                    Szy206TypeEnum szy206TypeEnum=getTypeEnum(szy206InfoDTO.getDataType());
                    //数据值
                    Double resultData=Double.parseDouble(result);
                    mainBody.put(szy206TypeEnum.getKey(),resultData);
                    mainHead.put(szy206TypeEnum.getKey(),szy206TypeEnum.getDescription());

                    log.info("遥测地址："+szy206InfoDTO.getStationAddr()+" 类型："+szy206TypeEnum.getDescription()+" 结果："+result);
                }catch (Exception e){
                    log.error("解析正文异常", e);
                }
            }

        }
    }

    public static Szy206TypeEnum getTypeEnum(String typeKey){
        for(Szy206TypeEnum szy206TypeEnum:Szy206TypeEnum.values()){
            if(szy206TypeEnum.getKey().equals(typeKey)){
                return szy206TypeEnum;
            }
        }
        return null;
    }


    public static String char2String(char[] ch,Integer decimalPoint,Boolean isNegative){
        //将char 2位分为一组 并倒序排列
        int length=ch.length/2;
        String[] dataArray=new String[length];
        for(int i=0;i<ch.length;i+=2){
            String c=ch[i]+""+ch[i+1];
            dataArray[length-1]=c;
            length--;
        }


        StringBuilder stringBuilder=new StringBuilder();

        for(int j=0;j<dataArray.length;j++){
            if(j==0){
                if(isNegative){
                    //第一位代表+ -值
                    if(Integer.parseInt(dataArray[j].substring(0,1))!=0){
                        stringBuilder.append("-");
                    }else{
                        stringBuilder.append("+");
                    }
                    stringBuilder.append(dataArray[j].substring(1,2));
                }else{
                 stringBuilder.append(dataArray[j]);
                }
            }else{
                stringBuilder.append(dataArray[j]);
            }
        }
        String resultDataStr=stringBuilder.toString();
        //添加小数点
        if(decimalPoint!=0){
            resultDataStr=stringBuilder.toString().substring(0,decimalPoint)+"."+stringBuilder.toString().substring(decimalPoint,stringBuilder.length());
        }

        return resultDataStr;
    }

    public static String hexString2binaryString(String hexString) {
        //16进制转10进制
        BigInteger sint = new BigInteger(hexString, 16);
        //10进制转2进制
        String result = sint.toString(2);
        //字符串反转
        return new StringBuilder(result).reverse().toString();
    }

    public static void main(String[] args) {
        String s = "68 1A 68 B3 53 03 81 0D 00 C0 00 59 00 00 00 82 61 10 00 00 01 10 60 00 00 45 08 09 0A 35 16".replace(" ", "");
        System.out.println(Szy206Parser.dataParser(s));
    }
}



//	/**
//     * 多项式 X^7+X^6+X^5+X^2+1  (0xE5)
//     * @param str
//     * @return
//     */
//    public static String calcCrc8(String str) {
//        byte[] data = HexUtil.decodeHex(str);
//        int crc = 0x00;
//        int dxs = 0xE5;
//        int hibyte;
//        int sbit;
//        for (byte datum : data) {
//            crc = crc ^ datum;
//            for (int j = 0; j < 8; j++) {
//                sbit = crc & 0x80;
//                crc = crc << 1;
//                if (sbit != 0) {
//                    crc ^= dxs;
//                }
//
//            }
//        }
//        String code = Integer.toHexString(crc & 0xff).toUpperCase();
//        return code;
//    }
