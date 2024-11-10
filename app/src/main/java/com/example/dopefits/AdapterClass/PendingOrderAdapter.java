    package com.example.dopefits.AdapterClass;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import com.example.dopefits.DataModel.orders;
    import com.example.dopefits.R;
    import java.util.List;

    public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {
        private List<orders> orderList;
        private OnOrderAcceptedListener orderAcceptedListener;

        public interface OnOrderAcceptedListener {
            void onOrderAccepted(orders order);
        }

        public PendingOrderAdapter(List<orders> orderList, OnOrderAcceptedListener listener) {
            this.orderList = orderList;
            this.orderAcceptedListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pending_order_item, parent, false); // Ensure item_order.xml exists
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            orders order = orderList.get(position);
            holder.textOrderId.setText("Order ID: " + order.getOrderId());
            holder.textItemName.setText(order.getProductName());
            holder.textOrderStatus.setText("Status: " + order.getOrderStatus());
            holder.textOrderDate.setText("Date: " + order.getOrderDate());

            double priceInDollars = order.getOrderTotal() != null ? Double.parseDouble(order.getOrderTotal()) / 100 : 0;
            holder.textItemPrice.setText("Price: ₱" + String.format("%.2f", priceInDollars));

            holder.btnAcceptOrder.setOnClickListener(v -> {
                if (orderAcceptedListener != null) {
                    orderAcceptedListener.onOrderAccepted(order);
                    removeOrder(order);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }

        public void removeOrder(orders order) {
            int position = -1;
            for (int i = 0; i < orderList.size(); i++) {
                if (orderList.get(i).getOrderId().equals(order.getOrderId())) {
                    position = i;
                    break;
                }
            }
            if (position >= 0) {
                orderList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, orderList.size());
            }
        }

        public void updateOrderStatus(String orderId, String status) {
            for (int i = 0; i < orderList.size(); i++) {
                if (orderList.get(i).getOrderId().equals(orderId)) {
                    orderList.get(i).setOrderStatus(status);
                    notifyItemChanged(i); // Notify the adapter of the change
                    break;
                }
            }
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textOrderId, textItemName, textItemPrice, textOrderStatus, textOrderDate;
            Button btnAcceptOrder;

            public ViewHolder(View itemView) {
                super(itemView);
                textOrderId = itemView.findViewById(R.id.textOrderId);
                textItemName = itemView.findViewById(R.id.textItemName);
                textItemPrice = itemView.findViewById(R.id.textItemPrice);
                textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
                textOrderDate = itemView.findViewById(R.id.textOrderDate);
                btnAcceptOrder = itemView.findViewById(R.id.buttonAcceptOrder);
            }
        }

    }
