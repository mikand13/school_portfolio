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
        
        // constructor adds Managers to the GameManager and sets state and direction
        public GameManager(LevelManager levelManager) {
            GameObject.OnDeathListener += DeathListener;
            LevelManager = levelManager;
            InputManager = new InputManager();
            CollisionManager = new CollisionManager();
            SpawnManager = new SpawnManager();

            GameState = EGameState.Running;
            OnGameStateChangedListener(GameState);

            CurrentDirection = EDirection.South;
            OnDirectionChangedListener(CurrentDirection);

            GameLoop();
        }

        private void GameLoop() {
            var t = new Stopwatch();
            t.Start();

            while (GameState != EGameState.Over){
                if (GameState != EGameState.Paused){
                    if (t.ElapsedMilliseconds < 100)
                        continue;
                    t.Restart();                    
                        
                    // check input
                    CheckInput();

                    // move all moveables                    
                    foreach (IMoveable t1 in LevelManager.Moveables){
                        t1.UpdatePosition();
                    }

                    // check collisions among all collidables
                    var collisions = CollisionManager.CheckCollision(LevelManager.Collidables);
                    if (collisions != null){
                        ApplyCollisions(collisions);
                    }

                    // check to see if there are still players playing
                    var playersLeft = false;
                    foreach (var go in LevelManager.GameObjects) {
                        if (!(go is IPlayer)) continue;
                        playersLeft = true;
                    }

                    if (!playersLeft) {
                        GameState = EGameState.Over;
                        OnGameStateChangedListener(GameState);
                    }

                    // render all renderables
                    if (GameState == EGameState.Running){
                        Console.Clear();
                        foreach (IRenderable t1 in LevelManager.Renderables){
                            t1.Render();
                        }
                    }
                } else {
                    // CheckInput if paused
                    CheckInput();
                }
            }
        }

        private void CheckInput(){
            Object obj = InputManager.PollKey(CurrentDirection);

            // checking return from inputmanager if not paused
            if (obj != null && GameState != EGameState.Paused) {
                if (obj is ConsoleKeyInfo) {
                    var cki = (ConsoleKeyInfo)obj;
                    if (cki.Key == ConsoleKey.Escape) {
                        GameState = EGameState.Over;
                    } else if (cki.Key == ConsoleKey.Spacebar) {
                        GameState = EGameState.Paused;
                        OnGameStateChangedListener(GameState);
                    }
                    OnGameStateChangedListener(GameState);
                } else if (obj is EDirection) {
                    CurrentDirection = (EDirection)obj;
                    OnDirectionChangedListener(CurrentDirection);
                }
            // checking return from inputmanager if paused
            } else if (obj != null){
                if (obj is ConsoleKeyInfo){
                    var cki = (ConsoleKeyInfo) obj;
                    if (cki.Key == ConsoleKey.Spacebar){
                        GameState = GameState == EGameState.Running ? EGameState.Paused : EGameState.Running;
                        OnGameStateChangedListener(GameState);
                    }
                }
            }
        }

        private void ApplyCollisions(IEnumerable<Collision> collisions){
            foreach (var c in collisions){
                c.Collidable1.OnCollision(c.Collidable2);
                c.Collidable2.OnCollision(c.Collidable1);
            }
        }

        private void DeathListener(GameObject go){
            LevelManager.Remove(go);

            if (go is IRespawnable){
                var newGo = Activator.CreateInstance(go.GetType());

                do{
                    SpawnManager.Spawn((GameObject) newGo, LevelManager.Width, LevelManager.Height);
                } while (!(CollisionManager.CheckRespawnCollision((GameObject) newGo, LevelManager.Collidables)));

                LevelManager.AddGameObject((GameObject) newGo);
            }
        }
    }
}
