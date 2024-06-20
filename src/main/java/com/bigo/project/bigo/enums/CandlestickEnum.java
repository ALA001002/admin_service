package com.bigo.project.bigo.enums;

/**
 * 行情粒度：1min, 5min, 15min, 30min, 60min, 1day, 1mon, 1week, 1year
 * @author Administrator
 */
public enum CandlestickEnum {
  /**
   * 1分钟
   */
  MIN1("1min"),
  /**
   * 5分钟
   */
  MIN5("5min"),
  /**
   * 15分钟
   */
  MIN15("15min"),
  /**
   * 30分钟
   */
  MIN30("30min"),
  /**
   * 60分钟
   */
  MIN60("60min"),
  /**
   * 4小时
   */
  HOUR4("4hour"),
  /**
   * 1天
   */
  DAY1("1day"),
  /**
   * 1周
   */
  WEEK1("1week"),
  /**
   * 1个月
   */
  MON1("1mon"),
  /**
   * 1年
   */
  //YEAR1("1year"),
  ;

  private final String code;

  CandlestickEnum(String code) {
    this.code = code;
  }

  public String getCode(){
    return code;
  }

}
