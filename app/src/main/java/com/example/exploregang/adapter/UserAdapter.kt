package com.example.exploregang.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.exploregang.R
import com.example.exploregang.data.model.User
import com.example.exploregang.databinding.ItemUserBinding
import com.example.exploregang.util.Utils

class UserAdapter (
    private val listener: OnClickUserListener,
    private val context: Context
): RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    private var users:ArrayList<User> =ArrayList()

    interface OnClickUserListener {
        fun onClickUser(user:User?)
        fun onLongClickUser(user: User?)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ItemUserBinding

        init {
            binding = ItemUserBinding.bind(itemView)
        }

        //SOLUCIÓN 1 y 2: se inicializa el listener y su dependencia a cada elemento intenView
        fun bind(listener:OnClickUserListener,user:User?) {
            itemView.setOnClickListener { view: View? ->
                listener.onClickUser(user)
            }
            itemView.setOnLongClickListener { view:View->
                listener.onLongClickUser(user)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding.root)
    }
    fun update(data: Collection<User>) {
        users.clear()
       users.addAll(data)
        //Importante: si no se llama a este método no se actualiza el recyclerviwer
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val user=users[position]
        if(!user.imageId.isNullOrEmpty()){
          Utils.getImage(user.imageId,holder.binding.ivPhoto)
        }else{
            holder.binding.ivPhoto.setImageResource(R.drawable.usericon)
            holder.binding.ivPhoto.setPadding(20)
        }
        holder.binding.tvUserName.setText(user.name)
        holder.bind(listener,user)
    }

    override fun getItemCount(): Int {
       return users.size
    }

    fun clear() {
        users.clear()
    }

}
