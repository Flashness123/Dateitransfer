package beleg;

import java.io.IOException;

public interface FT {
  /**
   *
   * @return whether file request was transmitted correctly
   * @throws IOException if connection fails, or File too large
   */

  boolean file_req() throws IOException;

  /**
   *
   * @return whether server was initiated correctly
   * @throws IOException if connection fails
   */

  boolean file_init() throws IOException;
}
