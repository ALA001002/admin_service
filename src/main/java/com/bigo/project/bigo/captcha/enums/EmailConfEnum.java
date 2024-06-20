package com.bigo.project.bigo.captcha.enums;

public enum EmailConfEnum {


    EMAIL_01("no_reply@ant-exchange.com","ssmwetbvapqwxaex","Ant Exchange"),
    EMAIL_02("noreply@ant-exchange.com","hmwxwmhhfbydmmib","Ant Exchange"),
    EMAIL_03("do-not-reply@ant-exchange.com","mafdpsayroxcivjh","Ant Exchange"),
    ;

    /**
     * 变更类型
     */
    private String userName;
    /**
     * 变更类型名称
     */
    private String password;

    private String sendName;

    EmailConfEnum(String userName, String password, String sendName){
        this.userName = userName;
        this.password = password;
        this.sendName = sendName;
    }

    public String getUserName() {
        return userName;
    }



    public String getPassword() {
        return password;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public static EmailConfEnum randomUser(EmailConfEnum[] values){
        return values[(int)(Math.random()*values.length)];
    }
}
