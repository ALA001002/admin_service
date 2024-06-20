package com.bigo.project.bigo.captcha.enums;

public enum EmailConfEnum2 {


//    EMAIL_01("noreply@bycoin66.com","mhhrwspurydmrhbt"),
    EMAIL_02("no_reply@kuomri.com","mrqukkbwetxwhuzl","Kuomri"),
    EMAIL_03("noreply@kuomri.com","qcxpwbmisnyikqom","Kuomri"),
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

    EmailConfEnum2(String userName, String password, String sendName){
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


    public static EmailConfEnum2 randomUser(EmailConfEnum2[] values){
        return values[(int)(Math.random()*values.length)];
    }
}
