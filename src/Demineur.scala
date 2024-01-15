import hevs.graphics.FunGraphics

import java.awt.{Color, Polygon}
import java.awt.event.{KeyEvent, KeyListener, MouseEvent, MouseListener}
import scala.util.Random

/***
 * class permettant de lancer une partie de démineur en mode cmd ou en mode graphique
 * @param dimention dimention du démineur souhaité
 */
class Demineur (var dimention:Int){
  private var nbBomb:Int = 0 // nombre de bombes dans la partie
  private var nbCellFind:Int = 0 // nombre de cases trouvées
  private var window:FunGraphics = null // fenêtre du démineur
  private var end: Boolean = false // boolean indicant la fin de la partie (true si la partie est fini)

  private var mouseListener:MouseListener = new MouseListener {
    override def mouseClicked(e: MouseEvent): Unit = onCellClick(e)

    override def mouseReleased(e: MouseEvent): Unit = {}

    override def mouseEntered(e: MouseEvent): Unit = {}

    override def mousePressed(e: MouseEvent): Unit = {}

    override def mouseExited(e: MouseEvent): Unit = {}
  }

  private var keyListener:KeyListener = new KeyListener {
    override def keyTyped(e: KeyEvent): Unit = onKeyPress(e)

    override def keyPressed(e: KeyEvent): Unit = {}

    override def keyReleased(e: KeyEvent): Unit = {}
  }

  // contrôle de  la saisie de l'utilisateur pour la dimention du démineur
  if(dimention < 5)
    dimention = 5
  if(dimention > 14)
    dimention = 15

  private val grid:Array[Array[Cell]] = Array.ofDim(dimention,dimention) // grille contenant le demineur


  //=== Fonctions pour le mode graphique ===//

  /***
   * fonction permettant de commancer une partie en mode graphique
   * @param nbBomb
   */
  def startGameWindow(nbBomb: Int): Unit = {
    // création d'une nouvelle fenêtre et ajout des listener
    window = new FunGraphics(dimention * 20, dimention * 20)
    window.addMouseListener(mouseListener)
    window.setKeyManager(keyListener)

    // reset des variable pour gérer la partie
    nbCellFind = 0
    end = false
    this.nbBomb = nbBomb

    // contrôle que le nombre de bombes souhaité soit cohérent
    if (nbBomb >= dimention * dimention || nbBomb <= 0) {
      this.nbBomb = dimention
    }

    // setup de la grille du démineur
    setupGrid(this.nbBomb)
    // setup de l'affichage graphique
    setupGridWindow()
    // affichage de la réponse dans le terminal pour tester
    //showResult() // pour tester

  }

  /***
   * fonction permettant de jouer un coup dans le mode graphique
   * @param x pos x de la case
   * @param y pos y de la case
   * @param button boutton de la sourie cliqué
   */
  private def playWindow(x: Int, y: Int, button: Int): Unit = {

    // contrôle si la partie est terminée
    if (!end) {
      // contrôle si c'est un clique gauche ou droite (button = 1 si clique gauche) pour effectuer la bonne action
      if (button == 1 && !grid(x)(y).isFlag) {
        // contrôle si la case est une bombe
        // si oui: message de fin et affichage du résultat sur la console
        // si non: afficher les bonnes cases et mettre à jour l'affichage sur la fenêtre
        if (checkCell(x, y)) {
          // variable de fin de partie à mettre à true
          end = true

          // message de défaite dans la console
          println("Vous êtes tombé sur une bombe vous avez perdu!")

          // message de défaite sur la fenêtre
          window.clear()
          window.drawString(nbBomb / 2 * 20 - 40, 20, "Vous avez perdu!")
          window.drawString(nbBomb / 2 * 20 - 50, 40, "Appuyez sur espace")
          window.drawString(nbBomb / 2 * 20 - 45, 60, "pour recommencer.")

          // affichage du résultat sur la console
          showResult()
        } else {
          // afficher les cases nécessaires
          displayCells(x, y)
          // mise à jour du visuel
          showGridWindow()
        }

        // contrôle c'est la partie est gagnée
        if (nbCellFind == dimention * dimention - nbBomb) {
          // met fin à la partie
          end = true

          // affiche le message de fin dans la console
          println("Vous avez Gagné")

          // affiche le message de fin dans la console
          window.clear()
          window.drawString(nbBomb / 2 * 20 - 40, 20, "Vous avez Gagné!")
          window.drawString(nbBomb / 2 * 20 - 50, 40, "Appuyez sur espace")
          window.drawString(nbBomb / 2 * 20 - 45, 60, "pour recommencer.")
        }
      }
      else if (button == 3) {
        // contrôle que l'utilisateur n'essaie pas de mettre un drapeau sur une case déjà révélé
        if (grid(x)(y).isHide) {
          // affiche ou enlève le drapeau de la case
          grid(x)(y).setFlag()

          // met à jour le visuel
          showGridWindow()
        }
      }
    }
  }

  /***
   * fonction permettant de setup la grille au début dans la fenêtre
   */
  private def setupGridWindow(): Unit = {
    for (x <- grid.indices; y <- grid(x).indices) {
      //dessine la grille sur la fenêtre
      window.drawRect(x * 20, y * 20, 20, 20)
    }
  }

  /***
   * fonction permettant d'afficher la grille actuelle sur la fenêtre
   */
  private def showGridWindow(): Unit =
  {
    // parcoure tout le tableau
    for (y <- grid.indices) {
      for (x <- grid(y).indices) {
        // si c'est un drapeau un D rouge est dessiné sur la case
        if (grid(x)(y).isFlag) {
          window.drawString(x * 20, (y + 1) * 20, "D", Color.red, 20)
        }
        // si la case est scencé être caché la fonction dessine un D blanc pour la supression des drapeau si besoin
        else if (grid(x)(y).isHide) {
          window.drawString(x * 20, (y + 1) * 20, "D", Color.white, 20)
        }
        // si la case n'est pas déjà correctement dessiné alors la fonction dessine le bon numéro et annonce que la case est dessiné
        else if (!grid(x)(y).isDraw) {
          window.drawString(x * 20, (y + 1) * 20, grid(x)(y).nbBomb.toString, Color.BLACK, 20)
          grid(x)(y).setDraw()
        }
      }
    }
  }

  /***
   * fonction s'executant à chaque touche du clavier pressé
   * @param e
   */
  private def onKeyPress(e: KeyEvent): Unit = {
    // restart une partie si la partie actuel est terminé et si la touche espace est pressé
    if (e.getKeyChar == 32 && end) {
      startGameWindow(nbBomb)
    }
  }

  /***
   * fonction s'executant à chaque clique de souris
   * @param e
   */
  private def onCellClick(e: MouseEvent): Unit = {
    // fait une action sur une case de la grille (soit découvrir une case soit mettre un drapeau)
    playWindow(e.getX / 20, (e.getY / 20), e.getButton)
  }



  //=== fonctions pour jouer en ligne de commande ===//

  /***
   * fonciton permettant de lancé une partie en mode cmd
   * @param nbBomb attend le nombre de bombes souhaitlées dans la partie
   */
  def startGame(nbBomb:Int): Unit = {
    // set les variables pour le début de partie
    nbCellFind = 0
    this.nbBomb = nbBomb

    // contrôle la cohérence de la saisie utilisateur sur le nombre de bombe
    if(nbBomb >= dimention*dimention || nbBomb <= 0){
      this.nbBomb = dimention
    }

    // setup le tableau
    setupGrid(this.nbBomb)

    // affiche le résultat pour tester le programe
    //showResult() // pour tester

    //commence la partie
    play()

  }

  /***
   * fonction permettant de faire les contrôles à chaque coup du joueur et de faire jouer le joueur
   */
  private def play(): Unit = {
    var end: Boolean = false // variable permettant d'annoncer la fin de partie

    // boucle qui va tourné jusqu'à que la partie soit finie
    do {

      var action: Char = ' ' // action que l'utilisateur veux faire pour son coup
      // pos x de la case visée
      var x: Int = 0
      // pos y de la case visée
      var y: Int = 0

      // lecture des données de l'utilisateur
      print(s"Entrer l'action que vous voulez faire(F pour poser un drapeau ou n'import quel autre caractère pour découvrir une case) ")
      action = Input.readChar()
      showAction()
      print(s"Entrer un la colonne(nombre entre 1 et $dimention) ")
      x = Input.readInt() - 1
      print(s"Entrer un la ligne(nombre entre 1 et $dimention) ")
      y = Input.readInt() - 1

      //contrôle si l'action selectionnée est la découverte de case pour permettre le bon contrôle de saisie
      if (!(action == 'F' || action == 'f')) {
        // boucle jusqu'à que les coordonées entré par l'utilisateur soit correctes
        while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
          reAskCo()

        }

        // boucle jusqu'à la coordonée est valide (pas déjà découverte ou si elle pointe sur un drapeau)
        while (grid(x)(y).isFlag || !grid(x)(y).isHide) {
          reAskCo()

          // boucle jusqu'à que les coordonées entré par l'utilisateur soit correctes
          while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
            reAskCo()
          }
        }

        // contrôle si la case séléctionnée est une bombe ou pas
        // si oui: partie perdu
        // si non: affichage des cases nécessaires
        if (checkCell(x, y)) {
          // met la variable en fin de partie
          end = true

          // affichage du message de défaite
          println("Vous êtes tombé sur une bombe vous avez perdu!")

          // affichage du résultat
          showResult()
        } else {
          // affiche la case pointée et les cases necéssaire
          displayCells(x, y)

          // affiche la grille mise à jour
          showGrid()
        }
      }
      else {
        // boucle jusqu'à que les coordonées entré par l'utilisateur soit correctes
        while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
          reAskCo()
        }

        // boucle jusqu'à la cases séléctionné pour mettre un drapeau soit valide (donc pas sur une case découverte)
        while (!grid(x)(y).isHide) {
          reAskCo()

          // boucle jusqu'à que les coordonées entré par l'utilisateur soit correctes
          while (x < 0 || x >= dimention || y < 0 || y >= dimention) {
            reAskCo()
          }
        }
        // affiche ou cache le drapeau sur la case
        grid(x)(y).setFlag()

        // affiche la grille mise à jour
        showGrid()
      }

      // contrôle si la partie est gagné
      if (nbCellFind == dimention * dimention - nbBomb) {
        // partie fini
        end = true

        // affiche le message de victoire
        println("Vous avez Gagné")
      }

      // fonction utilisée pour affiché l'action en cours pour le joueur
      def showAction(): Unit = {
        if (action == 'F' || action == 'f')
          println("Action: Poser un drapeau")
        else
          println("Action: Découvrir une case")
      }

      // fonction utilisée pour rédemandé des coordonées à l'utilisateur
      def reAskCo(): Unit = {
        println("Entrer des coordonées valides")
        showAction()
        print(s"Entrer un la colonne(nombre entre 1 et $dimention) ")
        x = Input.readInt() - 1
        print(s"Entrer un la ligne(nombre entre 1 et $dimention) ")
        y = Input.readInt() - 1
      }
    } while (!end)
  }

  /***
   * fonction permettant d'afficher la grille à n'importe que stade de la partie dans la console
   */
  private def showGrid() : Unit = {
    var res:String = "" // variable qui stock l'affichage de la grille
    // boucle sur toutes les cases de la grille
    for (y <- grid.indices) {
      for(x <- grid(y).indices){
        // si la case est un drapeau alors met un F
        if(grid(x)(y).isFlag){
          res += s"|F"

        }
        // si la cases est caché met rien
        else if(grid(x)(y).isHide){
          res += "| "
        }
        // affiche le nombre de la case
        else {
          res += s"|${grid(x)(y).nbBomb}"
        }
      }
      res += "|\n"
    }
    // affiche dans la console le résultat
    println(res)
  }

  /***
   * fonction permettant d'afficher la grille avec les réponses dans la console
   */
  def showResult(): Unit = {
    var res: String = "" // stock l'affichage de la grille

    // parcourt toutes la grille
    for (y <- grid.indices) {
      for (x <- grid(y).indices) {

        // si la case actuelle est une bombe alors affihche un B sinon affiche le nombre de la case
        if (grid(x)(y).isBomb)
          res += s"|B"
        else
          res += s"|${grid(x)(y).nbBomb}"

      }
      res += "|\n"
    }
    // affiche le résultat dans la console
    println(res)
  }


  //=== fonctions pour les deux modes de jeu ===//

  /***
   * fonction permettant de générer la grille
   * @param nbBomb attend le nombre de bombes souhaitées pour la partie
   */
  private def setupGrid(nbBomb:Int) : Unit = {
    // parcour tout le tableau pour instancier chaque case
    for(x <- grid.indices; y <- grid(x).indices){
      grid(x)(y) = new Cell()
    }

    // génère les bombes aléatoirement
    generBomb(nbBomb)

    // met les bons numéros dans chaque cases
    setupNbCell()
  }

  /***
   * fonction permettant de générer les chiffre sur les cases
   */
  private def setupNbCell(): Unit = {
    // parcoure tout le tableau est compte le nombre de bombe qu'il y a autour de la case et stoque le nombre dans la case
    // les conditions sont présentent pour gérer les cases qui sont sur les bords (bord droite, bord gauche, bord du haut,
    // bord du bas, coin bas gauche, coin bas droite, coin haut gauche, haut coin droite)
    for (x <- grid.indices) {
      for(y <- grid(x).indices){
        // ne fait rien si la case est une bombe
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

  /***
   * fonction permettant de générer les bombes dans la grille
   * @param nbBomb le nombre de bombes souhaitées dans la grille
   */
  private def generBomb(nbBomb:Int): Unit = {
    var bomb:Int = nbBomb // stoque le nombre de bombe à générer

    // contrôle la cohérence du nombre de bombes
    if(bomb > dimention * dimention || bomb < 1 ){
      bomb = dimention
    }

    // génèration des bombes
    for( nbrBombActu:Int <- 1 to bomb){
      // boucle jusqu'à que la bombe soit généré
      while(!grid(Random.nextInt(dimention))(Random.nextInt(dimention)).setBomb()){}
    }
  }

  /***
   * fonction permettant de contrôler si la case est une bombe
   * @param x pos x dans le tableau de la case
   * @param y pos y dans le tableau de la case
   * @return true si la case est une bombe et false sinon
   */
  private def checkCell(x: Int, y: Int): Boolean = {
    if (grid(x)(y).isBomb)
      return true
    false
  }

  /***
   *
   * @param x pos x dans le tableau de la case
   * @param y pos y dans le tableau de la case
   */
  private def setupDisplayCells(x: Int, y: Int): Unit = {
    // contrôle si la case est bien caché pour évité des boucle infini
    if (grid(x)(y).isHide) {
      // dévoile la case et fait les contrôle pour dévoiler les cases nécessaire
      displayCells(x, y)

    }
  }

  /***
   * fonction permettant d'afficher une case et de faire les contrôles sur les case adjacente pour les afficher aussi si nécessaire
   * @param x pos x dans le tableau de la case
   * @param y pos y dans le tableau de la case
   */
  private def displayCells(x: Int, y: Int): Unit = {
    // met la case en mode affiché
    grid(x)(y).displayCell()
    // incrémente le nombre de cases trouvées
    nbCellFind += 1

    // contrôle si le nombre de la case est égale à 0 pour faire découvrir et contrôler les cases ajdacentes
    if (grid(x)(y).nbBomb == 0) {

      // les conditions permettent de gérer les cases qui sont sur les bords
      if (x > 0 && x < grid.length - 1 && y > 0 && y < grid(x).length - 1) { // les cases pas sur les bords

        setupDisplayCells(x - 1, y - 1)
        setupDisplayCells(x - 1, y)
        setupDisplayCells(x, y - 1)
        setupDisplayCells(x + 1, y + 1)
        setupDisplayCells(x + 1, y)
        setupDisplayCells(x, y + 1)
        setupDisplayCells(x + 1, y - 1)
        setupDisplayCells(x - 1, y + 1)

      } else if (x > 0 && y > 0 && y < grid(x).length - 1) { // les case sur les bords de droite

        setupDisplayCells(x - 1, y - 1)
        setupDisplayCells(x - 1, y)
        setupDisplayCells(x, y - 1)
        setupDisplayCells(x, y + 1)
        setupDisplayCells(x - 1, y + 1)

      } else if (x < grid.length - 1 && y > 0 && y < grid(x).length - 1) { // les cases sur les bords de gauche

        setupDisplayCells(x, y - 1)
        setupDisplayCells(x + 1, y + 1)
        setupDisplayCells(x + 1, y)
        setupDisplayCells(x, y + 1)
        setupDisplayCells(x + 1, y - 1)

      } else if (x > 0 && x < grid.length - 1 && y < grid(x).length - 1) { // les cases sur les bords du haut

        setupDisplayCells(x - 1, y)
        setupDisplayCells(x + 1, y + 1)
        setupDisplayCells(x + 1, y)
        setupDisplayCells(x, y + 1)
        setupDisplayCells(x - 1, y + 1)

      } else if (x > 0 && x < grid.length - 1 && y > 0) { // les cases sur les bords du bas

        setupDisplayCells(x - 1, y - 1)
        setupDisplayCells(x - 1, y)
        setupDisplayCells(x, y - 1)
        setupDisplayCells(x + 1, y)
        setupDisplayCells(x + 1, y - 1)

      } else if (x < grid.length - 1 && y < grid(x).length - 1) { // la case sur le coins haut gauche

        setupDisplayCells(x + 1, y + 1)
        setupDisplayCells(x + 1, y)
        setupDisplayCells(x, y + 1)

      } else if (x > 0 && y < grid(x).length - 1) { // // la case sur le coins haut droite

        setupDisplayCells(x, y + 1)
        setupDisplayCells(x - 1, y + 1)
        setupDisplayCells(x - 1, y)

      } else if (x > 0 && y > 0) { // la case sur le coins bas droite

        setupDisplayCells(x - 1, y - 1)
        setupDisplayCells(x - 1, y)
        setupDisplayCells(x, y - 1)

      } else { // la case sur le coin bas gauche

        setupDisplayCells(x, y - 1)
        setupDisplayCells(x + 1, y - 1)
        setupDisplayCells(x + 1, y - 1)

      }
    }
  }
}