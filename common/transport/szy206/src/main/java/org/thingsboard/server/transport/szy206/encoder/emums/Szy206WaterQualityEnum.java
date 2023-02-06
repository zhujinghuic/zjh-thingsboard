package org.thingsboard.server.transport.szy206.encoder.emums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CaoLongMin
 * @version 1.0
 * @description TODO
 * @since 2022/6/24
 */
@Getter
@AllArgsConstructor
public enum Szy206WaterQualityEnum {
    D0("D0","水温","℃","N(3,1)"),
    D1("D1","pH","","N(4,2)"),
    D2("D2","溶解氧","mg/L","N(4,2)"),
    D3("D3","高锰酸盐指数","mg/L","N(4,2)"),
    D4("D4","电导率","µs/cm","N(5,0)"),
    D5("D5","氧化还原电位","mv","N(5,1)"),
    D6("D6","浊度","度","N(3,0)"),
    D7("D7","化学需氧量","mg/L","N(7,1)"),
    D8("D8","五日生化需氧量","mg/L","N(5,1)"),
    D9("D9","氨氮","mg/L","N(6,2)"),
    D10("D10","总氮","mg/L","N(5,2)"),
    D11("D11","铜","mg/L","N(7,4)"),
    D12("D12","锌","mg/L","N(6,4)"),
    D13("D13","氟化物","mg/L","N(5,2)"),
    D14("D14","硒","mg/L","N(7,5)"),
    D15("D15","砷","mg/L","N(7,5)"),
    D16("D16","汞","mg/L","N(7,5)"),
    D17("D17","镉","mg/L","N(7,5)"),
    D18("D18","六价铬","mg/L","N(5,3)"),
    D19("D19","铅","mg/L","N(7,5)"),
    D20("D20","氰化物","mg/L","N(5,3)"),
    D21("D21","挥发酚","mg/L","N(5,3)"),
    D22("D22","苯酚","mg/L","N(5,2)"),
    D23("D23","硫化物","mg/L","N(5,3)"),
    D24("D24","粪大肠菌群","个／L","N(10,0)"),
    D25("D25","硫酸盐","mg/L","N(6,2)"),
    D26("D26","氯化物","mg/L","N(8,2)"),
    D27("D27","硝酸盐氮","mg/L","N(5,2)"),
    D28("D28","铁","mg/L","N(4,2)"),
    D29("D29","锰","mg/L","N(4,2)"),
    D30("D30","石油类","mg/L","N(4,2)"),
    D31("D31","阴离子表面活性剂","mg/L","N(4,2)"),
    D32("D32","六六六","mg/L","N(7,6)"),
    D33("D33","滴滴涕","mg/L","N(7,6)"),
    D34("D34","有机氯农药","mg/L","N(7,6)"),
    D35("D35","总磷","mg/L","N(5,3)"),
    D36("D36","叶绿素","μg/L","N(6,3)");

    /**
     * 关键词
     */
    private String key;

    /**
     * 关键词
     */
    private String name;
    /**
     * 数据库字段
     */
    private String unit;
    /**
     * 精度
     */
    private String precision;
}
