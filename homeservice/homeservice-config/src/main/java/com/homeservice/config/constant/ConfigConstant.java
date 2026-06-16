package com.homeservice.config.constant;

public class ConfigConstant {

    private ConfigConstant() {
    }

    public static final String GROUP_UPLOAD = "upload";
    public static final String GROUP_PAYMENT = "payment";
    public static final String GROUP_SMS = "sms";
    public static final String GROUP_MAP = "map";
    public static final String GROUP_SYSTEM = "system";

    public static final String KEY_UPLOAD_DOMAIN = "upload.domain";
    public static final String KEY_UPLOAD_MAX_FILE_SIZE = "upload.max_file_size";
    public static final String KEY_UPLOAD_ALLOWED_TYPES = "upload.allowed_types";

    public static final String KEY_WECHAT_APP_ID = "payment.wechat.app_id";
    public static final String KEY_WECHAT_MCH_ID = "payment.wechat.mch_id";
    public static final String KEY_WECHAT_API_KEY = "payment.wechat.api_key";
    public static final String KEY_WECHAT_NOTIFY_URL = "payment.wechat.notify_url";

    public static final String KEY_ALIPAY_APP_ID = "payment.alipay.app_id";
    public static final String KEY_ALIPAY_PRIVATE_KEY = "payment.alipay.private_key";
    public static final String KEY_ALIPAY_PUBLIC_KEY = "payment.alipay.public_key";
    public static final String KEY_ALIPAY_NOTIFY_URL = "payment.alipay.notify_url";

    public static final String KEY_SMS_PROVIDER = "sms.provider";
    public static final String KEY_SMS_ACCESS_KEY = "sms.access_key";
    public static final String KEY_SMS_SECRET_KEY = "sms.secret_key";
    public static final String KEY_SMS_SIGN_NAME = "sms.sign_name";
    public static final String KEY_SMS_TEMPLATE_CODE = "sms.template_code";

    public static final String KEY_MAP_API_KEY = "map.api_key";

    public static final String KEY_APP_NAME = "system.app_name";
    public static final String KEY_APP_VERSION = "system.app_version";
    public static final String KEY_CUSTOMER_SERVICE_PHONE = "system.customer_service_phone";
}