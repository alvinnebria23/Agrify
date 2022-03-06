package com.example.agrify;

public class Constants {
    public static final String IP_ADDRESS  = "http://192.168.43.101:8080/";
    public static final String ROOT_URL    = IP_ADDRESS+"agrifyPHPScript/";

    public static final String PUSH_NOTIFICATION = "pushnotification";

    public static final String PATH_PROFILE_IMAGES_FOLDER       = IP_ADDRESS+"profileImages/";
    public static final String PATH_CROPS_IMAGES_FOLDER         = IP_ADDRESS+"/agridev_website/image_crops/";
    public static final String PATH_LIVESTOCKS_IMAGES_FOLDER    = IP_ADDRESS+"/agridev_website/image_livestocks/";
    public static final String PATH_POULTRIES_IMAGES_FOLDER     = IP_ADDRESS+"/agridev_website/image_poltry/";
    public static final String PATH_FISHERIES_IMAGES_FOLDER     = IP_ADDRESS+"/agridev_website/image_fisheries/";
    public static final String PATH_DEMAND_IMAGES_FOLDER        = IP_ADDRESS+"DemandImages/";

    public static final String URL_REGISTER                 = ROOT_URL+"registerUser.php";
    public static final String URL_LOGIN                    = ROOT_URL+"userLogin.php";
    public static final String URL_RETRIEVE_DEMAND          = ROOT_URL+"retrieveDemand.php";
    public static final String URL_UPLOAD_DEMAND_IMAGES     = ROOT_URL+"uploadDemandImages.php";
    public static final String URL_POPULATE_PRODUCT_SPINNER = ROOT_URL+"populateProductSpinner.php";
    public static final String URL_POPULATE_VARIETY_SPINNER = ROOT_URL+"populateVarietySpinner.php";
    public static final String URL_RETRIEVE_VENDOR_ID       = ROOT_URL+"retrieveVendorID.php";
    public static final String URL_SAVE_DEMAND_INFO         = ROOT_URL+"saveDemandInfo.php";
    public static final String URL_GET_LATEST_DEMAND_ID     = ROOT_URL+"getLatestDemandID.php";
    public static final String URL_RETRIEVE_DEMANDERS       = ROOT_URL+"retrieveDemanders.php";
    public static final String URL_RETRIEVE_DEMAND_IMAGES   = ROOT_URL+"retrieveDemandImages.php";
    public static final String URL_RETRIEVE_USER_PROFILE    = ROOT_URL+"retrieveUserProfile.php";
    public static final String URL_UPDATE_DEMAND            = ROOT_URL+"updateDemand.php";
    public static final String URL_UPDATE_USER_PROFILE      = ROOT_URL+"updateUserProfile.php";
    public static final String URL_RETRIEVE_MY_DEMANDS      = ROOT_URL+"retrieveMyDemands.php";
    public static final String URL_MANAGE_DEMAND            = ROOT_URL+"manageDemand.php";
    public static final String URL_RETRIEVE_DEMAND_INFO     = ROOT_URL+"retrieveDemandInfo.php";
    public static final String URL_UPDATE_DEMAND_INFO       = ROOT_URL+"updateDemandInfo.php";
    public static final String URL_RETRIEVE_STATUS          = ROOT_URL+"retrieveStatus.php";
    public static final String URL_SAVE_STATUS              = ROOT_URL+"saveStatus.php";
    public static final String URL_UPDATE_TOKEN             = ROOT_URL+"updateToken.php";
    public static final String URL_SEND_NOTIFICATION        = ROOT_URL+"sendNotification.php";
    public static final String URL_PROCESS_MESSAGE          = ROOT_URL+"processMessage.php";
    public static final String URL_RETRIEVE_QRCODE          = ROOT_URL+"retrieveQRCodeString.php";
    public static final String URL_RETRIEVE_ANALYSED_DATA   = ROOT_URL+"retrieveAnalysedData.php";
    public static final String URL_RETRIEVE_ANALYSED_INFO   = ROOT_URL+"retrieveAnalysedInfo.php";
}
