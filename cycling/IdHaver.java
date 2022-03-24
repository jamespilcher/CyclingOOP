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
   * Gets ID of object.
   *
   * @return int
   */
  public int getId() { 
    return id;
  }

  
  /** 
   * Sets ID of object.
   *
   * @param id value to set ID to
   */
  public void setId(int id) { 
    this.id = id;
  }
}
