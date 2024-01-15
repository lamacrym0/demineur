import hevs.graphics.FunGraphics

import java.awt.{Color, Polygon}
import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import scala.util.Random

/***
 *
 * @param dimention
 */
class Demineur (var dimention:Int){
  var nbBomb:Int = 0
  var nbCellFind:Int = 0
  var window:FunGraphics = null
  var end: Boolean = false
  var mouseListener:MouseListener = new MouseListener {
    override def mouseClicked(e: MouseEvent): Unit = onCellClick(e)

    override def mousePressed(e: MouseEvent): Unit = {}

    override def mouseReleased(e: MouseEvent): Unit = {}

    override def mouseEntered(e: MouseEvent): Unit = {}

    override def mouseExited(e: MouseEvent): Unit = {}
  }
  var keyListener:KeyListener = new KeyListener {
    override def keyTyped(e: KeyEvent): Unit = onKeyPress(e)

    override def keyPressed(e: KeyEvent): Unit = {}

    override def keyReleased(e: KeyEvent): Unit = {}
  }

  if(dimention < 5)
    dimention = 5
  if(dimention > 14)
    dimention = 15
  var grid:Array[Array[Cell]] = Array.ofDim(dimention,dimention)

  def play():Unit = {
    var end:Boolean = false

    do {
      var action:Char = ' '
      var x:Int = 0
      var y:Int = 0

      print(s"Entrer l'action que vous voulez faire(F pour poser un drapeau ou n'import quel autre caractère pour découvrir une case) ")
      action = Input.readChar()
      showAction()
      print(s"Entrer un la colonne(nombre entre 1 et $dimention) ")
      x = Input.readInt() - 1
      print(s"Entrer un la ligne(nombre entre 1 et $dimention) ")
      y = Input.readInt() - 1

      if (!(action == 'F' || action == 'f')) {
        while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
          reAskCo()

        }
        while (grid(x)(y).isFlag || !grid(x)(y).isHide || x < 0 || x >= dimention || y < 0 || y >= dimention) {
          reAskCo()
          while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
            reAskCo()
          }
        }
        if (checkCell(x, y)) {
          end = true
          println("Vous êtes tombé sur une bombe vous avez perdu!")
          showResult()
        } else {
          displayCells(x, y)
          showGrid()
        }

      }
      else {
        while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
          reAskCo()
        }
        while (!grid(x)(y).isHide || x < 0 || x >= dimention || y < 0 || y >= dimention) {
          reAskCo()
          while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
            reAskCo()
          }
        }
        grid(x)(y).setFlag()
        showGrid()
      }

      if(nbCellFind == dimention * dimention - nbBomb) {
        end = true
        println("Vous avez Gagné")
      }

      def showAction():Unit = {
        if (action == 'F' || action == 'f')
          println("Action: Poser un drapeau")
        else
          println("Action: Découvrir une case")
      }
      def reAskCo(): Unit = {
        println("Entrer des coordonées valides")
        showAction()
        print(s"Entrer un la colonne(nombre entre 1 et $dimention) ")
        x = Input.readInt() - 1
        print(s"Entrer un la ligne(nombre entre 1 et $dimention) ")
        y = Input.readInt() - 1
      }
    }while(!end)

  }

  def playWindow(x:Int,y:Int,button:Int): Unit = {

    if(!end){
      if(button == 1 && !grid(x)(y).isFlag){
        if (checkCell(x, y)) {
          end = true
          println("Vous êtes tombé sur une bombe vous avez perdu!")
          window.clear()
          window.drawString(nbBomb/2*20-40,20,"Vous avez perdu!")
          window.drawString(nbBomb/2*20-50,40,"Appuyez sur espace")
          window.drawString(nbBomb/2*20-45,60,"pour recommencer.")
          showResult()
        } else {
          displayCells(x, y)
          showGridWindow()
        }

        if (nbCellFind == dimention * dimention - nbBomb) {
          end = true
          println("Vous avez Gagné")
          window.clear()
          window.drawString(nbBomb / 2 * 20 - 40, 20, "Vous avez Gagné!")
          window.drawString(nbBomb / 2 * 20 - 50, 40, "Appuyez sur espace")
          window.drawString(nbBomb / 2 * 20 - 45, 60, "pour recommencer.")
        }
      }
      else if(button == 3){
        if(grid(x)(y).isHide) {
          grid(x)(y).setFlag()
          showGridWindow()
        }
      }

    }
  }

  def checkCell(x:Int,y:Int):Boolean = {
    if(grid(x)(y).isBomb)
      return true
    false
  }

  def setupDisplayCells(x:Int,y:Int):Unit = {
    if(grid(x)(y).isHide){
      grid(x)(y).displayCell()
      displayCells(x, y)

    }
  }

  def displayCells(x: Int, y: Int): Unit = {
    grid(x)(y).displayCell()
    nbCellFind += 1
      if(grid(x)(y).nbBomb == 0){

        if (x > 0 && x < grid.length - 1 && y > 0 && y < grid(x).length - 1) { // les cases pas sur les bords

          setupDisplayCells(x - 1,y - 1)
          setupDisplayCells(x - 1,y)
          setupDisplayCells(x,y - 1)
          setupDisplayCells(x + 1,y + 1)
          setupDisplayCells(x + 1,y)
          setupDisplayCells(x,y + 1)
          setupDisplayCells(x + 1,y - 1)
          setupDisplayCells(x - 1,y + 1)

        } else if (x > 0 && y > 0 && y < grid(x).length - 1) { // les case sur les bords de droite

          setupDisplayCells(x - 1,y - 1)
          setupDisplayCells(x - 1,y)
          setupDisplayCells(x,y - 1)
          setupDisplayCells(x,y + 1)
          setupDisplayCells(x - 1,y + 1)

        } else if (x < grid.length - 1 && y > 0 && y < grid(x).length - 1) { // les cases sur les bords de gauche

          setupDisplayCells(x,y - 1)
          setupDisplayCells(x + 1,y + 1)
          setupDisplayCells(x + 1,y)
          setupDisplayCells(x,y + 1)
          setupDisplayCells(x + 1,y - 1)

        } else if (x > 0 && x < grid.length - 1 && y < grid(x).length - 1) { // les cases sur les bords du haut

          setupDisplayCells(x - 1,y)
          setupDisplayCells(x + 1,y + 1)
          setupDisplayCells(x + 1,y)
          setupDisplayCells(x,y + 1)
          setupDisplayCells(x - 1,y + 1)

        } else if (x > 0 && x < grid.length - 1 && y > 0) { // les cases sur les bords du bas

          setupDisplayCells(x - 1,y - 1)
          setupDisplayCells(x - 1,y)
          setupDisplayCells(x,y - 1)
          setupDisplayCells(x + 1,y)
          setupDisplayCells(x + 1,y - 1)

        } else if (x < grid.length - 1 && y < grid(x).length - 1) { // la case sur le coins haut gauche

          setupDisplayCells(x + 1,y + 1)
          setupDisplayCells(x + 1,y)
          setupDisplayCells(x,y + 1)

        } else if (x > 0 && y < grid(x).length - 1) { // // la case sur le coins haut droite

          setupDisplayCells(x,y + 1)
          setupDisplayCells(x - 1,y + 1)
          setupDisplayCells(x - 1,y)

        } else if (x > 0 && y > 0) { // la case sur le coins bas droite

          setupDisplayCells(x - 1,y - 1)
          setupDisplayCells(x - 1,y)
          setupDisplayCells(x,y - 1)

        } else { // la case sur le coin bas gauche

          setupDisplayCells(x,y - 1 )
          setupDisplayCells(x+1,y-1)
          setupDisplayCells(x+1,y-1)

        }
      }
  }

  def startGame(nbBomb:Int): Unit = {
    nbCellFind = 0
    this.nbBomb = nbBomb
    if(nbBomb >= dimention*dimention || nbBomb <= 0){
      this.nbBomb = dimention
    }

    setupGrid(this.nbBomb)
    showResult() // pour tester
    play()

  }

  def setupGridWindow():Unit = {
    for (x <- grid.indices; y <- grid(x).indices) {
      window.drawRect(x*20,y*20,20,20)

    }
  }

  def startGameWindow(nbBomb: Int): Unit = {
    window = new FunGraphics(dimention*20,dimention*20)
    window.addMouseListener(mouseListener)
    window.setKeyManager(keyListener)
    nbCellFind = 0
    end = false
    this.nbBomb = nbBomb

    if (nbBomb >= dimention * dimention || nbBomb <= 0) {
      this.nbBomb = dimention
    }

    setupGrid(this.nbBomb)
    setupGridWindow()
    showResult() // pour tester

  }

  def showGrid() : Unit = {
    var res:String = ""
    for (y <- grid.indices) {
      for(x <- grid(y).indices){
        if(grid(x)(y).isFlag){
          res += s"|F"
        } else if(grid(x)(y).isHide){
          res += "| "
        }
        else {
          res += s"|${grid(x)(y).nbBomb}"
        }
      }
      res += "|\n"
    }
    println(res)
  }

  def showGridWindow(): Unit = {
    for (y <- grid.indices) {
      for (x <- grid(y).indices) {
        if (grid(x)(y).isFlag) {
          window.drawString(x*20,(y+1)*20,"D",Color.red,20)
        } else if (grid(x)(y).isHide) {
          window.drawString(x*20,(y+1)*20,"D",Color.white,20)
          window.drawRect(x*20,y*20,20,20)
        }
        else if(!grid(x)(y).isDraw){
          window.drawString(x*20,(y+1)*20,grid(x)(y).nbBomb.toString,Color.BLACK,20)
          grid(x)(y).setDraw()
        }
      }
    }
  }

  def onKeyPress(e:KeyEvent):Unit = {
    if(e.getKeyChar == 32 && end){
      startGameWindow(nbBomb)
    }
  }

  def onCellClick(e:MouseEvent): Unit = {
    println(s"x: ${e.getX / 20}, y: ${e.getY / 20}")
    playWindow(e.getX / 20,(e.getY / 20),e.getButton)
  }

  def showResult(): Unit = {
    var res: String = ""
    for (y <- grid.indices) {
      for (x <- grid(y).indices) {
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
      grid(x)(y) = new Cell()
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

    for( nbrBombActu:Int <- 1 to nbBomb){
      while(!grid(Random.nextInt(dimention))(Random.nextInt(dimention)).setBomb()){}
    }

  }
}