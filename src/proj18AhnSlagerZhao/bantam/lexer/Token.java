 /*
  * @(#)Token.java                        2.0 1999/08/11
  *
  * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
  * and School of Computer and Math Sciences, The Robert Gordon University,
  * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
  * All rights reserved.
  *
  * This software is provided free for educational use only. It may
  * not be used for commercial purposes without the prior written permission
  * of the authors.
  *
  * Modified by Dale Skrien, Fall 2018
  */

 package proj18AhnSlagerZhao.bantam.lexer;

 import java.util.Set;

 public class Token
 {
     //instance variables
     public Kind kind;
     public String spelling; // the actual sequence of chars in the token
     public int position; // the line number where the token was found

     public String getSpelling() {
         return spelling;
     }


     //constructor
     Token(Kind kind, String spelling, int position) {
         this.spelling = spelling;
         this.position = position;

         // patch the kind in the case of boolean constants and keywords
         if (kind == Kind.IDENTIFIER && (spelling.equals("true") || spelling.equals("false"))) {
             this.kind = Kind.BOOLEAN;
         }
         else if (kind == Kind.IDENTIFIER && reservedWords.contains(spelling)) {
             this.kind = Enum.valueOf(Kind.class, spelling.toUpperCase());
         }
         else {
             this.kind = kind;
         }
     }

     public String toString() {
         return "Token: Kind=" + kind.name() + ", spelling=" + spelling + ", " +
                 "position=" + position;
     }


     public enum Kind
     {
         NOTE, PITCH, OCTAVE, CHORD, CHORDTYPE, TIMESIG, KEY, STEPSIZE, LENGTH, STEPLENGTH, STEPFREQ, PATTERN,
         // literals, identifiers...
         INTCONST, STRCONST, BOOLEAN, IDENTIFIER, DIV, MINUS,

         // punctuation...
         DOT, COLON, SEMICOLON, COMMA,

         // brackets...
         LPAREN, RPAREN, LCURLY, RCURLY,

         // special tokens...
         COMMENT, ERROR, EOF, //end of file token

         // reserved words
         PIECE, VERSE, CHORUS, BRIDGE, RIGHTHAND, LEFTHAND, BASELINE, LAYOUT, ORDEROBJ, CRESCENDO
     }

     private static Set<String> reservedWords = Set.of("piece", "verse", "chorus", "bridge", "righthand", "lefthand",
             "baseseq", "layout", "timesig", "crescendo", "key", "chord", "start", "stepsize", "length", "stepfreq", "author");
 }