package cycling;

/**
 * Contains attributes and methods to do with IDs, each class that needs an ID inherits this.
 *
 * @author James Pilcher
 * @author Daniel Moulton
 * @version 1.0
 */
public class IdHaver {
  private int id;

  /** 
   * @return int
   */
  public int getId() { 
    return id;
  }

  /** 
   * @param id
   */
  public void setId(int id) { 
    this.id = id;
  }
}
