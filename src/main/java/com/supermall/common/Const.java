package com.supermall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface Role{
        //普通用户
        int ROLE_CUSTOMER = 0;
        //管理员
        int ROLE_ADMIN = 1;
    }

    public interface CartChecked{
        //即购物车选中状态
        int CHECKED = 1;
        //购物车未被选中状态
        int UN_CHECKED = 0;
    }

    public interface CartLimitCount{
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }


    /**
     * 价格的升序和降序处理常量
     */
    public interface ProductListOrderBy{

        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");
     }

    public enum ProductStatusEnu{
        ON_SALE(1,"在线");
        private String value;
        private int code;

        ProductStatusEnu(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
