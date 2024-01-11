/***
 *
 * @param x pos x de la case
 * @param y
 */
class Cell(var nbBomb:Int = 0) {
  var isBomb:Boolean = false
  var isFlag:Boolean = false
  var isHide:Boolean = true

  /***
   * met la bombe dans la case
   * @return flase si la case est déjà une bombe, sinon trasforme la case en bombe et return true
   */
  def setBomb(): Boolean = {
    if(isBomb)
      return false

    isBomb = true
    true
  }

  /**
   *
   * @return true si la case a un drapeau, sinon flase
   */
  def setFlag():Unit = {
    isFlag = !isFlag
  }

  def displayCell():Boolean = {
    if(! isHide)
      return false

    isHide = false
    true
  }




}
