package net.liftweb.mapper

/*                                                *\
 (c) 2006-2007 WorldWide Conferencing, LLC
 Distributed under an Apache License
 http://www.apache.org/licenses/LICENSE-2.0
 \*                                                */

import java.sql.{ResultSet, Types}
import java.lang.reflect.Method
import net.liftweb.util.Helpers._
// import java.lang.{Integer}
import net.liftweb.http.S
import java.util.Date
import net.liftweb.util._

class MappedBoolean[T<:Mapper[T]](val owner : T) extends MappedField[Boolean, T] {
  private var data : Can[Boolean] = Full(defaultValue)
  def defaultValue = false

  def dbFieldClass = classOf[Boolean]
  
  /**
   * Get the JDBC SQL Type for this field
   */
  def targetSQLType = Types.BOOLEAN

  protected def i_is_! = data openOr false
  
  protected def real_i_set_!(value : Boolean) : Boolean = {
    dirty_?(data.map(_ != value) openOr true)
    data = Full(value)
    value
  }
  override def readPermission_? = true
  override def writePermission_? = true
  
  def real_convertToJDBCFriendly(value: Boolean): Object = new java.lang.Integer(if (value) 1 else 0)
  
  def jdbcFriendly(field : String) = data.map(v => new java.lang.Integer(if(v) 1 else 0)) openOr null

  def ::=(in : Any) : Boolean = {
    in match {
      case b: Boolean => this := b
      case (b: Boolean) :: _ => this := b
      case Some(b: Boolean) => this := b
      case None => this := false
      case (s: String) :: _ => this := toBoolean(s)
      case null => this := false
      case s: String => this := toBoolean(s)
      case o => this := toBoolean(o)
    }
  }

  protected def i_obscure_!(in : Boolean) = false
  
  def buildSetActualValue(accessor : Method, inst : AnyRef, columnName : String) : (T, AnyRef) => Unit = {
    inst match {
      case null => {(inst : T, v : AnyRef) => {val tv = getField(inst, accessor).asInstanceOf[MappedBoolean[T]]; tv.data = Full(false)}}
      case _ => {(inst : T, v : AnyRef) => {val tv = getField(inst, accessor).asInstanceOf[MappedBoolean[T]]; tv.data = Full(toBoolean(v))}}
    }
  }
  
  def buildSetLongValue(accessor : Method, columnName : String) : (T, Long, Boolean) => Unit = {
    {(inst : T, v: long, isNull: Boolean ) => {val tv = getField(inst, accessor).asInstanceOf[MappedBoolean[T]]; tv.data = if (isNull) Empty else Full(v != 0L)}}
  }
  def buildSetStringValue(accessor : Method, columnName : String) : (T, String) => Unit  = {
    {(inst : T, v: String ) => {val tv = getField(inst, accessor).asInstanceOf[MappedBoolean[T]]; tv.data = if (v == null) Empty else Full(toBoolean(v))}}
  }
  def buildSetDateValue(accessor : Method, columnName : String) : (T, Date) => Unit   = {
    {(inst : T, v: Date ) => {val tv = getField(inst, accessor).asInstanceOf[MappedBoolean[T]]; tv.data = if (v == null) Empty else Full(true)}}
  }
  def buildSetBooleanValue(accessor : Method, columnName : String) : (T, Boolean, Boolean) => Unit   = {
    {(inst : T, v: Boolean, isNull: Boolean ) => {val tv = getField(inst, accessor).asInstanceOf[MappedBoolean[T]]; tv.data = if (isNull) Empty else Full(v)}}
  }
  
  /**
   * Given the driver type, return the string required to create the column in the database
   */
  def fieldCreatorString(dbType: DriverType, colName: String): String = colName+" "+(dbType match {
    case MySqlDriver => "BOOLEAN"
    case DerbyDriver => "SMALLINT"
  })
  

  
    /**
   * Create an input field for the item
   */
  override def toForm = S.checkbox(is,this(_))
}

