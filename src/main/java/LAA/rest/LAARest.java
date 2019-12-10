package LAA.rest;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import LAA.service.LAAService;

@Path("/laa")
public class LAARest {
	
	private final LAAService lAAService = new LAAService();
	
	@Path("/uploadFile")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			InputStream uploadedInputStream) {
		return lAAService.uploadFile(uploadedInputStream);
	}
	
	
	@Path("/getMetrics/{metric}/{timestamp}/{type}")
	@GET
	public Response getMetrics(@PathParam("metric") String metric, 
			@PathParam("timestamp") long timestamp,
			@PathParam("type") String type) {
		return lAAService.getMetrics(metric, timestamp, type);
	}
	
}
