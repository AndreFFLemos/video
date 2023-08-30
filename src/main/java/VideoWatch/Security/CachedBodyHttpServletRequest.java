package VideoWatch.Security;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


//This class reads the request body once, stores it,
// and then allows the body to be read multiple times without any issues.
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        cacheRequestBody(request);
    }

    private void cacheRequestBody(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
        InputStream inputStream = request.getInputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            cachedContent.write(buffer, 0, bytesRead);
        }

        this.cachedBody = cachedContent.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(new ByteArrayInputStream(cachedBody));
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {

        private InputStream cachedBodyInputStream;

        public CachedBodyServletInputStream(InputStream cachedBodyInputStream) {
            this.cachedBodyInputStream = cachedBodyInputStream;
        }

        @Override
        public boolean isFinished() {
            try {
                return cachedBodyInputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return cachedBodyInputStream.read();
        }
    }
}
