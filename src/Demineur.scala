import scala.util.Random

/***
 *
 * @param dimention
 */
class Demineur (var dimention:Int){
  var nbBomb:Int = 0
  if(dimention < 5)
    dimention = 5
  var grid:Array[Array[Cell]] = Array.ofDim(dimention,dimention)

  def play():Unit = {
    var end:Boolean = false
    var nbCellFind:Int = 0

    do{
      print(s"Entrer un la colonne(nombre entre 1 et $dimention) ")
      var x = Input.readInt() - 1
      print(s"Entrer un la ligne(nombre entre 1 et $dimention) ")
      var y = Input.readInt() - 1

      if(checkCell(x,y)){
        end = true
        println("Vous êtes tombé sur une bombe vous avez perdu!")
        showResult()
      } else {
        grid(x)(y).setHide()
        showGrid()
        nbCellFind += 1
      }

      if(nbCellFind == dimention * dimention - nbBomb) {
        end = true
        println("Vous avez Gagné")
      }
    }while(!end)
  }

  def checkCell(x:Int,y:Int):Boolean = {
    if(grid(x)(y).isBomb)
      return true
    false
  }

  def startGame(nbBomb:Int): Unit = {
    this.nbBomb = nbBomb
    for (x <- grid.indices; y <- grid(x).indices) {
      grid(x)(y) = new Cell()
    }


    setupGrid(dimention)
    showResult() // pour tester
    play()

  }
  def showGrid() : Unit = {
    var res:String = ""
    for (x <- grid.indices) {
      for(y <- grid(x).indices){
        if(grid(x)(y).isHide){
          res += "| "
        }
        else {
          if (grid(x)(y).isBomb)
            res += s"|B"
          else
            res += s"|${grid(x)(y).nbBomb}"
        }
      }
      res += "|\n"
    }
    println(res)
  }

  def showResult(): Unit = {
    var res: String = ""
    for (x <- grid.indices) {
      for (y <- grid(x).indices) {
        if (grid(x)(y).isBomb)
          res += s"|B"
        else
          res += s"|${grid(x)(y).nbBomb}"

      }
      res += "|\n"
    }
    println(res)
  }

  def setupGrid(nbBomb:Int) : Unit = {
    for(x <- grid.indices; y <- grid(x).indices){
      grid(x)(y) = new Cell(x,y)
    }
    generBomb(nbBomb)

    setupNbCell()
  }

  def setupNbCell(): Unit = {
    for (x <- grid.indices) {
      for(y <- grid(x).indices){
        if (!grid(x)(y).isBomb) {

          if (x > 0 && x < grid.length - 1 && y > 0 && y < grid(x).length - 1) { // les case pas sur les bords
            var nb: Int = 0

            if (grid(x - 1)(y - 1).isBomb)
              nb += 1
            if (grid(x - 1)(y).isBomb)
              nb += 1
            if (grid(x)(y - 1).isBomb)
              nb += 1
            if (grid(x + 1)(y + 1).isBomb)
              nb += 1
            if (grid(x + 1)(y).isBomb)
              nb += 1
            if (grid(x)(y + 1).isBomb)
              nb += 1
            if (grid(x + 1)(y - 1).isBomb)
              nb += 1
            if (grid(x - 1)(y + 1).isBomb)
              nb += 1

            grid(x)(y).nbBomb = nb

          } else if (x > 0 && y > 0 && y < grid(x).length - 1) { // les case sur les bords de droite
            var nb: Int = 0

            if (grid(x - 1)(y - 1).isBomb)
              nb += 1
            if (grid(x - 1)(y).isBomb)
              nb += 1
            if (grid(x)(y - 1).isBomb)
              nb += 1
            if (grid(x)(y + 1).isBomb)
              nb += 1
            if (grid(x - 1)(y + 1).isBomb)
              nb += 1


            grid(x)(y).nbBomb = nb

          } else if (x < grid.length - 1 && y > 0 && y < grid(x).length - 1) { // les cases sur les bords de gauche
            var nb: Int = 0

            if (grid(x)(y - 1).isBomb)
              nb += 1
            if (grid(x + 1)(y + 1).isBomb)
              nb += 1
            if (grid(x + 1)(y).isBomb)
              nb += 1
            if (grid(x)(y + 1).isBomb)
              nb += 1
            if (grid(x + 1)(y - 1).isBomb)
              nb += 1


            grid(x)(y).nbBomb = nb

          } else if (x > 0 && x < grid.length - 1 && y < grid(x).length - 1) { // les cases sur les bords du haut
            var nb: Int = 0

            if (grid(x - 1)(y).isBomb)
              nb += 1
            if (grid(x + 1)(y + 1).isBomb)
              nb += 1
            if (grid(x + 1)(y).isBomb)
              nb += 1
            if (grid(x)(y + 1).isBomb)
              nb += 1
            if (grid(x - 1)(y + 1).isBomb)
              nb += 1

            grid(x)(y).nbBomb = nb

          } else if (x > 0 && x < grid.length - 1 && y > 0) { // les cases sur les bords du bas
            var nb: Int = 0

            if (grid(x - 1)(y - 1).isBomb)
              nb += 1
            if (grid(x - 1)(y).isBomb)
              nb += 1
            if (grid(x)(y - 1).isBomb)
              nb += 1
            if (grid(x + 1)(y).isBomb)
              nb += 1
            if (grid(x + 1)(y - 1).isBomb)
              nb += 1

            grid(x)(y).nbBomb = nb

          } else if (x < grid.length - 1 && y < grid(x).length - 1) { // la case sur le coins haut gauche
            var nb: Int = 0


            if (grid(x + 1)(y + 1).isBomb)
              nb += 1
            if (grid(x + 1)(y).isBomb)
              nb += 1
            if (grid(x)(y + 1).isBomb)
              nb += 1

            grid(x)(y).nbBomb = nb

          } else if (x > 0 && y < grid(x).length - 1) { // // la case sur le coins haut droite
            var nb: Int = 0

            if (grid(x)(y + 1).isBomb)
              nb += 1
            if (grid(x - 1)(y+1).isBomb)
              nb += 1
            if (grid(x - 1)(y).isBomb)
              nb += 1

            grid(x)(y).nbBomb = nb

          } else if (x > 0 && y > 0) { // la case sur le coins bas droite
            var nb: Int = 0

            if (grid(x - 1)(y - 1).isBomb)
              nb += 1
            if (grid(x - 1)(y).isBomb)
              nb += 1
            if (grid(x)(y - 1).isBomb)
              nb += 1

            grid(x)(y).nbBomb = nb

          } else { // la case sur le coin bas gauche
            var nb: Int = 0

            if (grid(x)(y - 1).isBomb)
              nb += 1
            if (grid(x + 1)(y - 1).isBomb)
              nb += 1
            if (grid(x + 1)(y).isBomb)
              nb += 1


            grid(x)(y).nbBomb = nb

          }

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

