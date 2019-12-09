package LAA.rest;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import LAA.service.LAAService;

@Path("/laa")
public class LAARest {
	
	private final LAAService lAAService = new LAAService();
	
	@Path("/uploadFile")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return lAAService.uploadFile(uploadedInputStream, fileDetail);
	}
	
	
	@Path("/getMetrics")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getMetrics(String metric, long timestamp, String type) {
		return lAAService.getMetrics(metric, timestamp, type);
	}
	
}
