import scala.util.Random

/***
 *
 * @param dimention
 */
class Demineur (dimention:Int){
  var grid:Array[Array[Cell]] = if(dimention > 4) Array.ofDim(dimention,dimention) else Array.ofDim(5,5)
  def startGame() : Unit = {
    val nbBomb = dimention
  }

  def startGame(nbBomb:Int): Unit = {
    for (x <- grid.indices; y <- grid(x).indices) {
      grid(x)(y) = new Cell(x, y)
    }
    if(nbBomb >= math.pow(dimention,2))
      generBomb(dimention)
    else
      generBomb(nbBomb)
  }

  def setupGrid() : Unit = {
    for(x <- grid.indices; y <- grid(x).indices){
      grid(x)(y) = new Cell(x,y)
    }
    generBomb(dimention)
  }

  def setupNbCell(): Unit = {
    for (x <- grid.indices; y <- grid(x).indices) {
      if(!grid(x)(y).isBomb){

        if(x >0 && x < grid.length - 1 && y>0 && y< grid(x).length-1){  // les case pas sur les bords
          var nb: Int = 0

          if (grid(x-1)(y-1).isBomb)
            nb += 1
          if (grid(x-1)(y).isBomb)
            nb += 1
          if (grid(x)(y-1).isBomb)
            nb += 1
          if (grid(x+1)(y+1).isBomb)
            nb += 1
          if (grid(x+1)(y).isBomb)
            nb += 1
          if (grid(x)(y+1).isBomb)
            nb += 1
          if (grid(x+1)(y-1).isBomb)
            nb += 1
          if (grid(x-1)(y+1).isBomb)
            nb += 1

          grid(x)(y).nbBomb = nb

        } else if(x>0 && y>0 && y< grid(x).length-1){   // les case sur les bords de droite
          var nb: Int = 0



          grid(x)(y).nbBomb = nb

        } else if (x < grid.length-1 && y > 0 && y < grid(x).length - 1) {  // les cases sur les bords de gauche
          var nb: Int = 0



          grid(x)(y).nbBomb = nb

        } else if (x >0 && x < grid.length - 1 && y< grid(x).length-1) { // les cases sur les bords du haut
          var nb: Int = 0


          grid(x)(y).nbBomb = nb

        } else{   // les cases sur les bords du bas
          var nb: Int = 0


          grid(x)(y).nbBomb = nb

        }

      }
    }
  }

  def generBomb(nbBomb:Int): Unit = {
    var nbNowBomb:Int = 0
    do{
      nbNowBomb +=1

      var bombOk = false

      do{
        bombOk = grid(Random.nextInt(nbBomb))(Random.nextInt(nbBomb)).setBomb()
      } while (!bombOk)

    }while(nbNowBomb < nbBomb)
  }
}

