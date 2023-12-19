/***
 *
 * @param x pos x de la case
 * @param y
 */
class Cell(var x:Int, var y:Int,var nbBomb:Int = 0) {
  var isBomb:Boolean = false
  var haveFlag:Boolean = false

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
  def setFlag():Boolean = {
    haveFlag = !haveFlag
    haveFlag
  }

}
