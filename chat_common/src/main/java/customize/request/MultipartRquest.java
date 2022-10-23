package customize.request;

import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.Data;

import java.io.InputStream;
import java.util.Map;

/**
 * @author yujie
 * @createTime 2022/10/23 20:19
 * @description
 */
@Data
public class MultipartRquest {
    private Map<String, FileUpload> fileUploadMap;
    private InputStream inputStream;
    private String contentType;
}
