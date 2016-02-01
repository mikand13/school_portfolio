using System;
using System.Collections.Generic;
using SnakeMess.Engine.Util;
using System.Diagnostics;

namespace SnakeMess.Engine {

    public class GameManager {
        public delegate void OnGameStateChanged(EGameState currentState);
        public static event OnGameStateChanged OnGameStateChangedListener;

        public delegate void OnDirectionChanged(EDirection currentDirection);
        public static event OnDirectionChanged OnDirectionChangedListener;

        private EGameState GameState { get; set; }
        private EDirection CurrentDirection { get; set; }
        private LevelManager LevelManager { get; set; }
        private InputManager InputManager { get; set; }
        private CollisionManager CollisionManager { get; set; }
        private SpawnManager SpawnManager { get; set; }
        private ScoreManager ScoreManager { get; set; }
        
        // constructor adds Managers to the GameManager and sets state and direction
        public GameManager(LevelManager levelManager) {
            GameObject.OnDeathListener += DeathListener;
            LevelManager = levelManager;
            InputManager = LevelManager.InputManager;
            CollisionManager = new CollisionManager();
            SpawnManager = new SpawnManager();
            ScoreManager = new ScoreManager();

            // sets random start direction
            SetStartDirection();           
            OnDirectionChangedListener(CurrentDirection);

            // loops through collidables and randomizes positions on collisions
            CheckValidStartPositions();

            if (LevelManager.StartMenu != null) {
                GameState = EGameState.Init;
                LevelManager.StartMenu.Render();
            } else {
                GameState = EGameState.Running;
            }

            OnGameStateChangedListener(GameState);
            GameLoop();
        }

        // helper for constructor
        private void SetStartDirection() {
            var rand = new Random();
            var direction = rand.Next(1, 4);
            switch (direction) {
                case 1:
                    CurrentDirection = EDirection.North;
                    break;
                case 2:
                    CurrentDirection = EDirection.South;
                    break;
                case 3:
                    CurrentDirection = EDirection.East;
                    break;
                case 4:
                    CurrentDirection = EDirection.West;
                    break;
            } 
        }

        // helper for constructor
        private void CheckValidStartPositions() {
            foreach (var c in LevelManager.Collidables) {
                // parses out player objects
                if (c is IPlayer)
                    continue;
                // parses out level object
                if (!(c is IRenderable))
                    continue;
                do {
                    SpawnManager.Spawn((GameObject) c, LevelManager.Width, LevelManager.Height);                    
                } while (!(CollisionManager.CheckRespawnCollision((GameObject) c, LevelManager.Collidables)));
            }
        }

        private void GameLoop() {
            var t = new Stopwatch();
            t.Start();

            while (GameState != EGameState.Exit){                 
                // game active
                if (GameState == EGameState.Running && t.ElapsedMilliseconds >= 100) {
                    t.Restart();                    
                    // check input
                    CheckInput();

                    // move all moveables                    
                    foreach (var t1 in LevelManager.Moveables){
                        t1.UpdatePosition();
                    }

                    // check collisions among all collidables
                    var collisions = CollisionManager.CheckCollision(LevelManager.Collidables);
                    if (collisions != null) {
                        ApplyCollisions(collisions);
                    }

                    // check to see if there are still players playing
                    var playersLeft = false;
                    var playerWon = false;
                    foreach (var go in LevelManager.GameObjects) {
                        if (!(go is IPlayer)) continue;
                        playersLeft = true;
                        if (go.Body.Count >= LevelManager.Height*LevelManager.Width)
                            playerWon = true;
                    }

                    // end game if no players or player won
                    if (!playersLeft || playerWon) {
                        GameState = EGameState.Over;
                        OnGameStateChangedListener(GameState);
                    }

                    // render all renderables
                    if (GameState == EGameState.Running) {
                        Console.Clear();
                        foreach (IRenderable t1 in LevelManager.Renderables){
                            t1.Render();
                        }
                    }                    
                    // game initializing
                } else switch (GameState) {
                    case EGameState.Init:
                        CheckInput();
                        break;
                    case EGameState.Over:
                        if (LevelManager.GameOver != null) {
                            LevelManager.GameOver.Render(ScoreManager.GetScore());
                        } else {
                            Environment.Exit(0);
                        }
                        while (GameState == EGameState.Over) {
                            CheckInput();
                        }
                        break;
                    case EGameState.Paused:
                        CheckInput();
                        break;
                }
            }
            StartupManager.Reset();
        }

        private void CheckInput() {
            Object obj = InputManager.PollKey(CurrentDirection, GameState);
            if (obj is EDirection)
                CurrentDirection = (EDirection) obj;
                OnDirectionChangedListener(CurrentDirection);
            if (obj is EGameState)
                GameState = (EGameState) obj;
                OnGameStateChangedListener(GameState);
        }

        private void ApplyCollisions(IEnumerable<Collision> collisions){
            foreach (var c in collisions){
                c.Collidable1.OnCollision(c.Collidable2);
                c.Collidable2.OnCollision(c.Collidable1);
            }
        }

        private void DeathListener(GameObject go){
            var points = go as IPointsValue;
            if (points != null) {
                ScoreManager.AddScore(points);
            } 

            var respawnable = go as IRespawnable;
            if (respawnable != null){
                var newGo = Activator.CreateInstance(respawnable.GetType());

                do{
                    SpawnManager.Spawn((GameObject) newGo, LevelManager.Width, LevelManager.Height);
                } while (!(CollisionManager.CheckRespawnCollision((GameObject) newGo, LevelManager.Collidables)));

                if (points != null) {
                    ((IPointsValue) newGo).Points = ((IPointsValue) go).Points;
                }

                LevelManager.AddGameObject((GameObject) newGo);
            }
            LevelManager.Remove(go);
        }
    }
}
