/**
  * Copyright 2023 json.cn 
  */
package tech.chowyijiu.huhu_bot.plugins.aliyunpan;
import java.util.List;

/**
 * Auto-generated: 2023-07-17 12:6:58
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class GitcafeResp {

    private boolean success;
    private List<Data> data;
    private String error;
    public void setSuccess(boolean success) {
         this.success = success;
     }
     public boolean getSuccess() {
         return success;
     }

    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

    public void setError(String error) {
         this.error = error;
     }
     public String getError() {
         return error;
     }

}