//package tech.chowyijiu.huhubot.plugins.fortnite;
//
//import lombok.Data;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author elastic chow
// * @date 5/6/2023
// */
//@Data
//public class ShopEntry {
//
//    private Integer regularPrice;
//    private Integer finalPrice;
//    //private boolean giftable;   //可送礼的
//    //private boolean refundable; //可退的
//    private Buddle bundle;
//    //private Integer sortPriority; //排序优先级
//    //private NewDisplayAsset newDisplayAsset;
//    private Item[] items;
//
//    @Data
//    static class NewDisplayAsset {
//        //private String id;
//        //private String cosmeticId;
//        private List<MaterialInstance> materialInstances;
//
//        @Data
//        static class MaterialInstance {
//            //private String id;
//            private Map<String, String> Images;
//        }
//    }
//
//    @Data
//    public static class Buddle {
//        private String name;
//        private String info;
//        private String image;
//    }
//
//    @Data
//    public static class Item {
//        private String id;
//        private String name;
//        private String description;
//        private Value type;
//        private Value rarity;
//        private Value introduction;
//        private Images images;
//        private LocalDateTime added;
//        private String[] shopHistory;
//
//        @Data
//        public static class Value {
//            //introduction
//            private String chapter; //2
//            private String season; //6
//            private String text; //"在第2章，第6赛季加入豪华阵容。"
//
//            private String value;
//            private String displayValue;
//            private String backendValue;
//        }
//
//        @Data
//        public static class Images {
//            private String smallIcon;
//            private String icon;
//            private String featured;
//        }
//    }
//
//}
